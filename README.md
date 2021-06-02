# <a name="top"></a>Context-Server

[![FIWARE Core Context Management](https://nexus.lab.fiware.org/repository/raw/public/badges/chapters/core.svg)](https://www.fiware.org/developers/catalogue/)
[![License badge](https://img.shields.io/github/license/FIWARE/context.Orion-LD.svg)](https://opensource.org/licenses/AGPL-3.0)
[![Docker badge](https://img.shields.io/docker/pulls/wistefan/context-server.svg)](https://hub.docker.com/r/wistefan/context-server/)
[![NGSI-LD badge](https://img.shields.io/badge/NGSI-LD-red.svg)](https://www.etsi.org/deliver/etsi_gs/CIM/001_099/009/01.04.01_60/gs_cim009v010401p.pdf)
[![Coverage Status](https://coveralls.io/repos/github/wistefan/context-server/badge.svg)](https://coveralls.io/github/wistefan/context-server)
[![Test](https://github.com/wistefan/context-server/actions/workflows/test.yml/badge.svg)](https://github.com/wistefan/context-server/actions/workflows/test.yml)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/4751/badge)](https://bestpractices.coreinfrastructure.org/projects/4751)
[![Known Vulnerabilities](https://snyk.io/test/github/wistefan/context-server/badge.svg)](https://snyk.io/test/github/wistefan/context-server)

The Context Server provides capabilities to serve and manage [NGSI-LD Contexts](https://www.etsi.org/deliver/etsi_gs/CIM/001_099/009/01.04.01_60/gs_cim009v010401p.pdf).
It provides multiple options for storage backends to allow environmental flexibility.     

The current api can be viewed here: [Swagger-UI](https://forge.etsi.org/swagger/ui/?url=https://raw.githubusercontent.com/wistefan/context-server/master/api/api.yaml)

## Configuration

The only general required configuration is the ```general.baseUrl```. It should be set to the url that ContextServer is available at, in order to be able 
to build a sufficent link-header on creation and list retrieval. The context URLs will be constructed after the following pattern:

```<BASE_URL>/jsonldContexts/<CONTEXT_ID>```, f.e. ```http://context-server.fiware.org/jsonldContexts/myFancyContext.json```

## Cache control

In order to provide information about context-caching, the [maxAge](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Controlg)
for [Cache-Control](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control) can be set. It will default to max(1 year) if not configured.

```general.maxAge:  31536000```

## Storage options

Multiple options for storage of the contexts are available. Every instance can only use one storage and multiple instances of distributed context servers
can only use one shared backend.

### In Memory

> :warning: In memory storage should only be used in single instance setups, since no distribution between multiple instances happens. It does not 
> persist anything.

Enable: ```memory.enabled: true```

### Local Disc

> :warning: Local Disc storage should only be used in single instance setups or setups with an underlying (transparently) distributed filesystem, since 
>no distribution between multiple instances happens.

Enable: ```local.enabled: true```

Properties:

|  Property | Env-Var | Description | Default | Required | 
| ----------------- | ----------------------------------- | ----------------------------------------------- | ------------------------ | ------------------------ | 
|```local.contextFolder``` | ```LOCAL_CONTEXTFOLDER``` | Local folder for the context's to be stored. | ```ld-contexts```|  ```true``` |

In order to use Local-Disk storage, the correct read and write permissions need to be set.

### GCS

Enable: ```gcs.enabled: true```

Properties:  

|  Property | Env-Var | Description | Default | Required | 
| ----------------- | ----------------------------------- | ----------------------------------------------- | ------------------------ | ------------------------ | 
|```gcs.bucketName``` | ```GCS_BUCKETNAME``` | Name of the bucket to be used for storage. | ```my-contexts```|  ```true``` |


In order to enable context storage on [GCS](https://cloud.google.com/storage/docs/creating-buckets), the [Google Service Account credentials](https://cloud.google.com/storage/docs/reference/libraries#setting_up_authentication)
need to be provided. A service-account json file should be created as described([Google-Doc](https://cloud.google.com/storage/docs/reference/libraries#setting_up_authentication))
and provided with the environment variable ```GOOGLE_APPLICATION_CREDENTIALS``` set to the file's path.

### FTP

Enable: ```ftp.enabled: true```

Properties:

|  Property | Env-Var | Description | Default | Required | 
| ----------------- | ----------------------------------- | ----------------------------------------------- | ------------------------ | ------------------------ | 
|```ftp.hostname``` | ```FTP_HOSTNAME``` | Hostname of the ftp to be used. | ```empty``` |  ```true``` |
|```ftp.port``` | ```FTP_PORT``` | Port of the ftp to be used. | ```21```|  ```true``` |
|```ftp.contextFolder``` | ```FTP_CONTEXTFOLDER``` | Folder to store the contexts at. | ```/my-contexts```|  ```true``` |
|```ftp.secured``` | ```FTP_SECURED``` | Does the ftp require username/password authentication | ```false```|  ```true``` |
|```ftp.username``` | ```FTP_USERNAME``` | Username to be used for authentication on ftp. | ```user```|  ```false``` |
|```ftp.password``` | ```FTP_PASSWORD``` | Password to be used for authentication on ftp. | ```password```|  ```false``` |