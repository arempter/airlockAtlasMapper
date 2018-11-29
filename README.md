### Example of custom AtlasResourceMapper 

#### Background

In order to sync tags between Apache Atlas and Ranger, [ranger-tagsync-service](https://cwiki.apache.org/confluence/display/RANGER/Tag+Synchronizer+Installation+and+Configuration).

By default it supports tag synchronization for Hadoop services like hive etc.

If you have custom types used in Atlas then additional java class is required to handle mapping, 
between those types in Atlas and service in Ranger.

#### How to run

1. Run standard mvn package command to build jar

```
package -DSkipTests
```   

2. Copy jar to lib directory (or class path) of tagsync service (ranger-1.x.0-tagsync/lib/)

3. Update config file - ranger-1.x.0-tagsync/install.properties and re-run setup.sh

4. Start serice you should see lines like:

```
29 Nov 2018 15:35:18 DEBUG AtlasResourceMapperUtil [main] - 117 <== initializeAtlasResourceMappers.initializeAtlasResourceMappers([org.apache.ranger.tagsync.source.atlas.AtlasHiveResourceMapper, 
org.apache.ranger.tagsync.source.atlas.AtlasHdfsResourceMapper, org.apache.ranger.tagsync.source.atlas.AtlasHbaseResourceMapper, org.apache.ranger.tagsync.source.atlas.AtlasKafkaResourceMapper, experimentation.AirlockMapper]): true
```

Done!

