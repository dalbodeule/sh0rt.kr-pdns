# Cloudflare compatible PowerDNS API Server

[![Discord](https://img.shields.io/discord/630271953806295051)](https://discord.gg/EPnCfz7xx5)

A Kotlin and Spring Boot API that exposes Cloudflare-style zone and DNS record endpoints backed by PowerDNS and MariaDB. API requests use `Authorization: Bearer <API_KEY>`.

## Build and run

Use JDK 25. The Gradle wrapper selects the Java 25 toolchain for compilation.

```sh
./gradlew test bootJar
./gradlew bootRun
```

`bootRun` activates the `dev` profile. Supply the environment variables below before starting the app. Tests use an in-memory H2 database and do not require MariaDB or PowerDNS.

| Variable | Description |
| --- | --- |
| `DB_HOST` | MariaDB host |
| `DB_PORT` | MariaDB port, usually `3306` |
| `DB_NAME` | Database name |
| `DB_USER` | Database user |
| `DB_PASSWORD` | Database password |
| `PDNS_API_KEY` | PowerDNS HTTP API key |
| `PDNS_API_URL` | PowerDNS HTTP API base URL, for example `http://pdns:8081` |
| `PDNS_NS` | Comma-separated nameservers, for example `ns1.example.com,ns2.example.com` |

## Publish AMD64 and ARM64 images

The script builds a Linux native image for each architecture with Paketo Buildpacks, pushes the architecture-specific tags with Docker CLI credentials, then publishes a multi-platform manifest. Docker, Buildx, and a registry login with push access to the target repository are required. On a host with a different architecture, Docker must support emulation for the target platform.

```sh
./scripts/build-multiarch-image.sh 1.0.0
```

The default repository is `${DOCKER_USERNAME:-dalbodeule}/dnsapi`. Set `IMAGE_REPOSITORY` to use another repository, such as `git.mori.space/dalbodeule/dnsapi`. The script publishes `:1.0.0-amd64`, `:1.0.0-arm64`, `:1.0.0`, and `:latest`; set `PUBLISH_LATEST=0` to leave `:latest` unchanged. Review the commands without publishing by setting `DRY_RUN=1`.

If a previous run built an architecture image locally but failed while pushing, log in to the registry and rerun with `RESUME=1`. The script checks the local image architecture and skips its costly rebuild.

```sh
docker login --username dalbodeule
RESUME=1 ./scripts/build-multiarch-image.sh 1.0.0
```

## Update API documentation

```sh
./gradlew updateDocs
```

This task starts the app in a test context, writes the current OpenAPI 3.1 specification to [openapi.json](openapi.json), and refreshes the generated API section below. `generateOpenApi` and `updateReadme` are also available separately. The documentation task uses H2 and does not contact PowerDNS.

## API reference

<!-- BEGIN GENERATED API -->
Generated from [openapi.json](openapi.json). Do not edit this section by hand.

### Endpoints

| Method | Path | Summary | Request body | Success response |
| --- | --- | --- | --- | --- |
| `GET` | `/zones` | Get all domains | — | 200: [ApiResponseDTOListDomainResponseDTO](#apiresponsedtolistdomainresponsedto) |
| `POST` | `/zones` | Create domain | [DomainRequestDTO](#domainrequestdto) | 200: [ApiResponseDTODomainResponseDTO](#apiresponsedtodomainresponsedto) |
| `GET` | `/zones/{cfid}` | Get domain | — | 200: [ApiResponseDTODomainResponseDTO](#apiresponsedtodomainresponsedto) |
| `DELETE` | `/zones/{domain_id}` | Delete domain | — | 200: [ApiResponseDTODeleteResponseWithId](#apiresponsedtodeleteresponsewithid) |
| `GET` | `/zones/{zone_id}/dns_records` | Get all records | — | 200: [ApiResponseDTOListRecordResponseDTO](#apiresponsedtolistrecordresponsedto) |
| `POST` | `/zones/{zone_id}/dns_records` | Add Record by ID | [RecordRequestDTO](#recordrequestdto) | 200: [ApiResponseDTORecordResponseDTO](#apiresponsedtorecordresponsedto) |
| `GET` | `/zones/{zone_id}/dns_records/{dns_record_id}` | Get Record by ID | — | 200: [ApiResponseDTORecordResponseDTO](#apiresponsedtorecordresponsedto) |
| `PUT` | `/zones/{zone_id}/dns_records/{dns_record_id}` | Update Record by ID | [RecordRequestDTO](#recordrequestdto) | 200: [ApiResponseDTORecordResponseDTO](#apiresponsedtorecordresponsedto) |
| `DELETE` | `/zones/{zone_id}/dns_records/{dns_record_id}` | Remove Record by ID | — | 200: [ApiResponseDTODeleteResponseWithId](#apiresponsedtodeleteresponsewithid) |

### Schemas

#### ApiResponseDTO

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `success` | boolean | No | — |
| `errors` | array of [ErrorOrMessage](#errorormessage) | No | — |
| `messages` | array of [ErrorOrMessage](#errorormessage) | No | — |
| `result` | — | No | — |

#### ApiResponseDTODeleteResponseWithId

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `success` | boolean | No | — |
| `errors` | array of [ErrorOrMessage](#errorormessage) | No | — |
| `messages` | array of [ErrorOrMessage](#errorormessage) | No | — |
| `result` | [DeleteResponseWithId](#deleteresponsewithid) or null | No | — |

#### ApiResponseDTODomainResponseDTO

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `success` | boolean | No | — |
| `errors` | array of [ErrorOrMessage](#errorormessage) | No | — |
| `messages` | array of [ErrorOrMessage](#errorormessage) | No | — |
| `result` | [DomainResponseDTO](#domainresponsedto) or null | No | — |

#### ApiResponseDTOListDomainResponseDTO

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `success` | boolean | No | — |
| `errors` | array of [ErrorOrMessage](#errorormessage) | No | — |
| `messages` | array of [ErrorOrMessage](#errorormessage) | No | — |
| `result` | array of [DomainResponseDTO](#domainresponsedto) or null | No | — |

#### ApiResponseDTOListRecordResponseDTO

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `success` | boolean | No | — |
| `errors` | array of [ErrorOrMessage](#errorormessage) | No | — |
| `messages` | array of [ErrorOrMessage](#errorormessage) | No | — |
| `result` | array of [RecordResponseDTO](#recordresponsedto) or null | No | — |

#### ApiResponseDTORecordResponseDTO

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `success` | boolean | No | — |
| `errors` | array of [ErrorOrMessage](#errorormessage) | No | — |
| `messages` | array of [ErrorOrMessage](#errorormessage) | No | — |
| `result` | [RecordResponseDTO](#recordresponsedto) or null | No | — |

#### DeleteResponseWithId

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `id` | string | No | — |

#### DomainRequestDTO

Request DTO for Domain

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `name` | string | No | Domain name(TLD) |

#### DomainResponseDTO

Response DTO for Domain

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `id` | string | No | Domain CFID |
| `name` | string | No | Domain name(TLD) |

#### ErrorOrMessage

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `code` | integer | No | — |
| `message` | string | No | — |

#### RecordRequestDTO

Request DTO for Record

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `type` | string | No | Record type |
| `name` | string | No | Host name |
| `value` | string | No | Record data |
| `ttl` | integer | No | TTL (Time to Live) |
| `priority` | integer or null | No | Priority |
| `proxied` | boolean | No | Proxied: cloudflare api compatibility |
| `comment` | string | No | comment |

#### RecordResponseDTO

Response DTO for Record

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `id` | string | No | Record ID |
| `type` | string | No | Record type |
| `name` | string | No | Record name |
| `content` | string | No | Record content |
| `zoneId` | string | No | Zone(TLD) ID |
| `zoneName` | string | No | Zone name(TLD) |
| `createdOn` | string | No | Record creation time |
| `modifiedOn` | string | No | Record modification time |
| `priority` | integer or null | No | Record priority |
| `proxiable` | boolean | No | is proxyable: must false |
| `proxied` | boolean | No | is proxied: must false |
| `ttl` | integer | No | Record TTL |
| `locked` | boolean | No | Record is locked: must false |
| `comment` | string or null | No | Record comments |
<!-- END GENERATED API -->
