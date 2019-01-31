# Synchronization

<!--DHIS2-SECTION-ID:sync-->

## Metadata

<!--DHIS2-SECTION-ID:sync_metadata-->

## Tracker data

<!--DHIS2-SECTION-ID:sync_tracker_data-->

### Download

Two main strategies to download tracker data: bulk or by uid.

#### Bulk download

Download a maximum of N trackedEntityInstances. 

- Paging is used to split the payload (page size 50). 
- If a page throws an error, it continues to the next.
- The download is finished when it gets N instances or the last call gives 0 results.
- Once finished, it looks for uncompleted tei of type RELATIONSHIP. They are downloaded one by one. Their relationships are not downloaded.

About scopes:

- Only CAPTURE scope is used to download.

#### By UID

Download a list of trackedEntityInstances by providing a uid list.

OPEN programs:

- No restrictions.

PROTECTED programs:

- 

### Upload

