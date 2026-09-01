# Scoped access { #android_sdk_scoped_access }

```kotlin
val scopedD2: ScopedD2 = d2.scopedTo(scope)
```

`ScopedD2` is the DHIS2 SDK narrowed to a subset of the data on the device. Code holding one can
read and write only what the scope grants, and cannot widen it.

It exists so that a host application can hand SDK access to code it does not fully trust — the
Capture App's plugin system is the first caller — without handing over `D2`. It is a general SDK
feature, not a plugin feature.

The important thing to understand first: **`ScopedD2` hands back ordinary SDK repositories.** There
is no wrapper API to learn. `scopedD2.events()` returns the same `EventCollectionRepository` you
would get from `d2.eventModule().events()`, already narrowed, so the whole fluent API — filters,
ordering, paging, `blockingGet()` — works unchanged.

```kotlin
val overdue = scopedD2.events()
    .byStatus().eq(EventStatus.OVERDUE)
    .byOrganisationUnitUid().eq(clinicUid)
    .orderByDueDate(RepositoryScope.OrderByDirection.ASC)
    .blockingGet()
```

## Describing a scope { #android_sdk_scoped_access_scope }

A scope is a `D2DataScope`: what may be read, what may be written, and which feature areas are
enabled at all.

```kotlin
val scope = D2DataScope(
    programs = UidScope.of("IpHINAT79UW"),
    dataSets = UidScope.None,
    trackedEntityTypes = UidScope.All,
    dataElements = UidScope.All,
    orgUnits = OrgUnitScope.of(setOf("ImspTQPwCqd"), OrganisationUnitMode.DESCENDANTS),
    writable = WritableScope(programs = UidScope.of("IpHINAT79UW")),
    capabilities = setOf(
        D2Capability.READ_METADATA,
        D2Capability.READ_TRACKED_ENTITY,
        D2Capability.WRITE_EVENT,
    ),
)
```

Three building blocks:

| Type | Values | Notes |
|---|---|---|
| `UidScope` | `All`, `None`, `Only(uids)` | `uidsOrNull()` returns `null` for unrestricted and an **empty set** for `None` — those mean opposite things |
| `OrgUnitScope` | `All`, `None`, `Capture`, `Only(uids, mode)` | `mode` is `SELECTED`, `CHILDREN` or `DESCENDANTS`; the roots are expanded on the device |
| `WritableScope` | `programs`, `dataSets`, `orgUnits` | **Always intersected with the read grant** |

Two rules worth internalising:

- **Closed by default.** Every dimension defaults to nothing. An empty scope grants nothing, not
  everything.
- **Writable is a subset, never an extension.** `writablePrograms()` is
  `programs.intersect(writable.programs)`, so naming a program in `writable` that is not readable
  grants nothing at all. The same holds for data sets and org units.

### Capabilities { #android_sdk_scoped_access_capabilities }

Capabilities gate whole feature areas, independently of which UIDs are granted. An empty capability
set exposes nothing however generous the UID grants are.

`READ_METADATA`, `READ_TRACKED_ENTITY`, `READ_ENROLLMENT`, `READ_EVENT`, `READ_DATA_VALUE`,
`SEARCH_TRACKED_ENTITY`, `READ_RELATIONSHIP`, `READ_FILE_RESOURCE`, and the four writes
`WRITE_TRACKED_ENTITY`, `WRITE_ENROLLMENT`, `WRITE_EVENT`, `WRITE_DATA_VALUE`.

Every accessor calls `requireCapability` before returning anything, so a missing capability throws
`D2Error(SCOPE_VIOLATION)` rather than returning an empty result. That distinction is deliberate:
"you may not look here" and "there is nothing here" are different answers and callers need to tell
them apart.

| Accessor | Capability |
|---|---|
| `programs()`, `programStages()`, `dataSets()`, `dataElements()`, `trackedEntityTypes()`, `organisationUnits()`, `trackedEntityAttributes()`, `optionSets()`, `options()`, `categoryCombos()`, `categoryOptionCombos()` | `READ_METADATA` |
| `trackedEntityInstances()`, `trackedEntityAttributeValues()` | `READ_TRACKED_ENTITY` |
| `enrollments()` | `READ_ENROLLMENT` |
| `events()`, `trackedEntityDataValues()` | `READ_EVENT` |
| `trackedEntitySearch()` | `SEARCH_TRACKED_ENTITY` |
| `dataValues()` | `READ_DATA_VALUE` |

## How it is enforced { #android_sdk_scoped_access_enforcement }

There are **four** distinct mechanisms. Knowing which one applies to a given accessor is the key to
working on this code safely.

```
                    ┌──────────────────────────────────────────────┐
  reads             │ 1. Append-only filters on RepositoryScope    │
  (most accessors)  │    by*() calls can only narrow further       │
                    └──────────────────────────────────────────────┘
  reads on tables   ┌──────────────────────────────────────────────┐
  with no program   │ 2. Mandatory SQL sub-select (guardedWith)    │
  column            └──────────────────────────────────────────────┘
                    ┌──────────────────────────────────────────────┐
  writes            │ 3. AccessGuard on the scope, checks the      │
                    │    object being written                      │
                    └──────────────────────────────────────────────┘
  tracker search    ┌──────────────────────────────────────────────┐
                    │ 4. TrackedEntityQueryGrant, re-applied on    │
                    │    every repository the fluent API builds    │
                    └──────────────────────────────────────────────┘
```

### 1. Reads: append-only filters { #android_sdk_scoped_access_reads }

`ScopedD2` pre-applies the grant with ordinary `by*()` calls before handing the repository over:

```kotlin
fun events(): EventCollectionRepository {
    requireCapability(D2Capability.READ_EVENT)
    var repo = d2.eventModule().events()
    scope.programs.uidsOrNull()?.let { repo = repo.byProgramUid().`in`(it.toList()) }
    resolver.readableOrgUnits()?.let { repo = repo.byOrganisationUnitUid().`in`(it.toList()) }
    return repo.guarded()
}
```

This cannot be undone, and that is a property of `RepositoryScope` rather than a check: every
`by*()` routes through `RepositoryScopeHelper`, which only ever does `filters + item`; the scope
field is `protected`; and no API removes, replaces or resets a filter. A caller's own filters are
AND-ed on top, so asking for something outside the grant returns **empty**, never wider results.

That is why an out-of-scope read is silent. Callers who need to know what they were granted should
read the scope rather than infer it from an empty list.

### 2. Reads via sub-select { #android_sdk_scoped_access_subselect }

`TrackedEntityAttributeValue` and `TrackedEntityDataValue` rows reference only their parent — there
is no program or org unit column to filter on. Restricting them needs SQL:

```kotlin
fun trackedEntityAttributeValues(): TrackedEntityAttributeValueCollectionRepository {
    requireCapability(D2Capability.READ_TRACKED_ENTITY)
    val repo = d2.trackedEntityModule().trackedEntityAttributeValues()
    val programs = scope.programs.uidsOrNull() ?: return repo.guarded()
    return repo.guardedWith(enrolledInProgramsClause(programs))
}
```

Without that clause a caller could read the attribute values of any tracked entity on the device by
UID. `guardedWith` adds the clause as a `RepositoryScopeComplexFilterItem`, which is as
unremovable as a filter.

### 3. Writes: a guard on the object { #android_sdk_scoped_access_writes }

Filters cannot protect writes. A create projection or value object carries its own org unit,
program and data element regardless of what the query said — so the object being written is what
gets checked, not the query that found it.

`ScopedAccessGuard` (implementing the internal `AccessGuard`) travels **on the `RepositoryScope`**,
which is what makes it unforgeable: the scope is copied field-by-field on every builder call, the
generated setter for `accessGuard` is `internal`, and nothing clears it. It therefore survives every
`by*()`, `orderBy*()`, `withChild()` and `uid()` in a fluent chain and cannot be removed by the code
being restricted.

It is consulted at every write entry point — `add()`, `set()`, `delete()`,
`dataValues().value(…)` — *before* the store is touched, so a refusal is whole rather than a
half-completed write:

```kotlin
override suspend fun suspendAdd(o: P): String {
    val obj = transformer.transform(o)
    // Deliberately outside the try: a scope violation must surface as SCOPE_VIOLATION rather than
    // be rewritten as OBJECT_CANT_BE_INSERTED.
    scope.accessGuard()?.checkWrite(obj)
    ...
}
```

One branch per writable type, and **unknown types are denied**:

| Object | Capability | Also checked |
|---|---|---|
| `TrackedEntityInstance` | `WRITE_TRACKED_ENTITY` | org unit, tracked entity type |
| `Enrollment` | `WRITE_ENROLLMENT` | writable program, org unit |
| `Event` | `WRITE_EVENT` | writable program, org unit |
| `DataValue` | `WRITE_DATA_VALUE` | writable data element, org unit |
| `TrackedEntityDataValue` | `WRITE_EVENT` | program and org unit of the parent event |
| `TrackedEntityAttributeValue` | `WRITE_TRACKED_ENTITY` | the entity is enrolled in a writable program |
| anything else | — | refused |

Denying by default matters: a model type added to the SDK later cannot become silently writable.

### 4. Tracker search: a re-applied grant { #android_sdk_scoped_access_search }

`trackedEntitySearch()` cannot use mechanism 1, and this is the subtlest part of the system.

Tracker search uses `TrackedEntityInstanceQueryRepositoryScope`, whose fields are **single-valued
and replaced** by `by*()` rather than appended: `program`, `trackedEntityType`, `orgUnits`,
`orgUnitMode`, and the online/offline mode. A caller could otherwise overwrite the grant simply by
asking for something else.

Two things close that:

- `TrackedEntitySearchOperators.scope` calls `requestedScope.applyGrant()`. Every `by*()` rebuilds
  the repository through that constructor, so a widening never survives the call that requested it.
- `applyGrant()` forces `RepositoryMode.OFFLINE_ONLY`. An online search is answered by the server,
  where none of these local restrictions exist, so a granted search is always local.
  `TrackedEntityInstanceQueryOnlineHelper.fromScope` refuses a granted scope outright as defence in
  depth.

Because the scope fields are single-valued, they cannot express "one of these" — the normal shape of
a grant. So the dimensions are enforced in two places:

| Dimension | Where | How |
|---|---|---|
| `orgUnits` | `applyGrant()` | the scope holds a *list*, so the granted set is substituted directly and the mode pinned to `SELECTED` |
| `programs` | `appendGrantWhere()` | sub-select on the enrollment table |
| `trackedEntityTypes` | `appendGrantWhere()` | `IN` clause on the tracked entity type column |

`applyGrant()` additionally rewrites a *caller-specified* program or type that falls outside the
grant to the sentinel `__scope_denied__`, which no UID can equal.

## Working on this code { #android_sdk_scoped_access_maintaining }

Rules to preserve. Each one has already been broken once.

1. **A new scope dimension needs a clause in both read paths.** Mechanism 1 (the `by*()` filter in
   `ScopedD2`) and mechanism 4 (`appendGrantWhere` / `applyGrant`) are separate. Adding a dimension
   to one and not the other means the same grant means different things depending on which accessor
   the caller used.
2. **A new writable type needs a branch in `ScopedAccessGuard.checkWrite`.** Without one it is
   refused — which is safe, but it will look like a bug, so add the branch deliberately.
3. **A new accessor calls `requireCapability` first**, and `guarded()` (or `guardedWith`) on the way
   out. `guarded()` is what installs the write guard; forgetting it makes the repository readable but
   unguarded for writes.
4. **Never render an empty grant as `IN ()`.** That is not valid SQL. Empty sets go through the
   `__scope_denied__` sentinel.
5. **Resolve grants lazily.** `ScopeResolver` caches org unit and data element resolution because
   both hit the database, and a scoped accessor may be called per row.
6. **Do not evaluate a refusal eagerly.** A guard that throws must be reached only on the path it
   guards — see the cautionary tale below.

### Two bugs, and what they teach { #android_sdk_scoped_access_bugs }

Both were found by exercising a real grant on a device, not by reading the code.

**Every scoped search threw.** `TrackedEntityInstanceQueryDataFetcher` resolved
`baseOnlineQueries = onlineHelper.fromScope(scope)` in a property initializer, and an `init` block
iterated it. Both ran whenever the fetcher was constructed — which happens for *every* query
regardless of mode. Since `fromScope` refuses a granted scope, the online guard fired on offline
searches too, and `trackedEntitySearch()` was unusable for any caller holding a grant. The guard
rejected exactly the case it exists to permit. Both properties are now `by lazy`.

*Lesson: a check that refuses must sit on the path it guards, not in a constructor.*

**The type grant was ignored.** `applyGrant()` only corrected a `trackedEntityType` the caller had
*named*, and `appendGrantWhere` bounded programs only. A caller who filtered by nothing got no type
restriction at all — a grant of one tracked entity type returned records of another. Meanwhile
`trackedEntityInstances()` had always applied the filter, so one grant meant two different things.

*Lesson: a grant has to be enforced for the caller who asks for nothing, not just the caller who
asks for the wrong thing.*

## What is deliberately withheld { #android_sdk_scoped_access_withheld }

`ScopedD2` exposes no accessor for these, by design:

- `databaseAdapter()` and `httpServiceClient()` — raw SQL and raw HTTP bypass every mechanism above.
- `wipeModule()` — destructive and outside any scope.
- `dataStoreModule()` — in the plugin use case it holds the configuration that defines the scope,
  so exposing it would let a caller widen its own grant.
- `userModule()` — credentials and account management.
- The sync and transport modules.
- Analytics and relationships. Both can reach outside a grant — a free-form dimension DSL, and
  object-graph traversal to tracked entities in other programs — and need their own design pass.

## Known limitations { #android_sdk_scoped_access_limitations }

Be honest about these when reasoning about the guarantee.

- **Same-process.** A scoped caller runs in the host process. Reflection, `internal` visibility
  (public in JVM bytecode), and the class loader of any object it legitimately holds are all
  available to it. Scoping makes the sanctioned path fully capable and every other path deliberate;
  it is not a sandbox. Real containment needs a separate process, which is why the grant and its
  enforcement live in the SDK — an IPC layer can sit in front of the same `ScopedD2`.
- `TrackedEntityInstanceQueryRepositoryScope` still has a **public constructor**, so a grant
  installed through the builder can in principle be bypassed by constructing a scope directly.
  Making it `internal` is a public-API break and needs a deprecation cycle.
- Capabilities are checked when a repository is **obtained**, not per operation. A caller holding a
  repository across a scope change would keep using it.
- `checkTrackedEntityAttributeValue` verifies the entity is enrolled in a writable program but does
  **not** check its org unit, unlike `checkEvent`. An unrestricted writable program grant therefore
  permits attribute writes outside the org unit grant.
- In the aggregate path the *guard* is covered (`refuse_a_data_value_for_a_data_element_outside_the_granted_data_sets`),
  but the resolution behind it is not: `ScopeResolver.readableDataElements()` turning granted data
  sets into data elements has no test, and it is the step that decides what `dataValues()` can see.

## Source map { #android_sdk_scoped_access_sources }

`core/src/main/java/org/hisp/dhis/android/core/`

| File | Role |
|---|---|
| `D2.kt` → `scopedTo()` | entry point |
| `scopedaccess/ScopedD2.kt` | every accessor, capability checks, read filters |
| `scopedaccess/D2DataScope.kt` | the scope model, `writablePrograms()`, `writableDataSets()` |
| `scopedaccess/UidScope.kt`, `OrgUnitScope.kt` | grant primitives |
| `scopedaccess/D2Capability.kt` | capability enum |
| `scopedaccess/internal/ScopeResolver.kt` | org unit hierarchy expansion, data element resolution, caching |
| `scopedaccess/internal/ScopedAccessGuard.kt` | the write guard, one branch per type |
| `arch/repositories/scope/internal/AccessGuard.kt` | the guard interface, carried on `RepositoryScope` |
| `trackedentity/search/TrackedEntityQueryGrant.kt` | the search grant and `applyGrant()` |
| `trackedentity/search/TrackedEntityInstanceLocalQueryHelper.kt` | `appendGrantWhere()` — programs and types |
| `trackedentity/search/TrackedEntityInstanceQueryOnlineHelper.kt` | online refusal |

### Tests { #android_sdk_scoped_access_tests }

| Test | Covers |
|---|---|
| `scopedaccess/UidScopeShould` | grant primitives, `intersect`, and that writable can never exceed readable |
| `scopedaccess/ScopeResolverShould` | the three org unit hierarchy modes, `All`/`None`/`Capture`, caching, and the writable intersection |
| `scopedaccess/ScopedAccessGuardShould` | the write guard — all six model branches, all four write capabilities, unknown type, null |
| `arch/repositories/scope/AccessGuardPropagationShould` | the guard survives filters, ordering and long fluent chains |
| `trackedentity/search/TrackedEntityQueryGrantShould` | `applyGrant()` — offline pinning, org unit substitution, `__scope_denied__` |
| `trackedentity/search/TrackedEntityInstanceLocalQueryHelperShould` | `appendGrantWhere()` — the program sub-select and the type clause |
| `trackedentity/search/ScopedSearchDataFetcherShould` | the online guard stays off the offline path |
| `androidTest .../ScopedD2MockIntegrationShould` | accessors end to end against a mock server — **device required**, so it does not run with the JVM unit tests |

**Gaps worth knowing before you rely on something.** `ScopeResolver.readableDataElements()` — the
step that turns granted data *sets* into the data *elements* `dataValues()` filters on — has no unit
test; only the org unit half of the resolver is covered. `OrgUnitScope` has no dedicated test, though
`ScopeResolverShould` exercises every one of its cases indirectly. And the accessor-level filtering in
`ScopedD2` itself is covered only by `ScopedD2MockIntegrationShould`, which needs a device — so a
plain JVM unit run verifies the *pieces* of the read path without ever assembling them.
