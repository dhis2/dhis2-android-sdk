# Maps { #android_sdk_maps }

> **Caution**
>
> This is an experimental feature and could change in future releases.

The SDK has a MapModule to download and access the information relative to maps in DHIS2.

By default, DHIS2 has some basemaps:

- OpenStreetMaps
- Bing: it is required to setup a bing api key in the server to make them work.

These map layers are downloaded in a separate call:

```java
d2.mapsModule().mapLayersDownloader().downloadMetadata()
```

Then, map layers can be accessed by using the corresponding collection repository, as usual:

```java
d2.mapsModule().mapLayers()
        .byName().eq("map_layer")
        .withImageryProviders()
        .get()
```

These map layers contain useful information to display them using a SDK for maps, in particular:

- imageUrl: it might look like `https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png`.
- subdomains: map providers usually offer a list of subdomains, for example `["a", "b", "c", "d"]`.
- subdomainPlaceholder: the token that must be replaced in the imageUrl, in this case `{s}`.

It is straightforward to get the list of imageUrls by iterating the subdomain list and using the placeholder to replace it in the url.
