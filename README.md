# Cloudflare compatible PowerDNS API Server

[![Discord](https://img.shields.io/discord/630271953806295051)](https://discord.gg/EPnCfz7xx5) [![Build Status](https://teamcity.mori.space/app/rest/builds/buildType:PdnsApi_Build/statusIcon)](https://teamcity.mori.space/project/PdnsApi)

> Version v1.0.0

## Path Table

| Method | Path | Description |
| --- | --- | --- |
| GET | [/zones](#getzones) | Get all domains |
| POST | [/zones](#postzones) | Create domain |
| GET | [/zones/{zone_id}/dns_records](#getzoneszone_iddns_records) | Get all records |
| POST | [/zones/{zone_id}/dns_records](#postzoneszone_iddns_records) | Add Record by ID |
| GET | [/zones/{zone_id}/dns_records/{dns_record_id}](#getzoneszone_iddns_recordsdns_record_id) | Get Record by ID |
| DELETE | [/zones/{zone_id}/dns_records/{dns_record_id}](#deletezoneszone_iddns_recordsdns_record_id) | Remove Record by ID |
| PUT | [/zones/{zone_id}/dns_records/{dns_record_id}](#putzoneszone_iddns_recordsdns_record_id) | Update Record by ID |
| GET | [/zones/{cfid}](#getzonescfid) | Get domain |
| DELETE | [/zones/{domain_id}](#deletezonesdomain_id) | Delete domain |

## Reference Table

| Name | Path | Description |
| --- | --- | --- |
| DomainRequestDTO | [#/components/schemas/DomainRequestDTO](#componentsschemasdomainrequestdto) | Request DTO for Domain |
| ApiResponseDTO | [#/components/schemas/ApiResponseDTO](#componentsschemasapiresponsedto) |  |
| ErrorOrMessage | [#/components/schemas/ErrorOrMessage](#componentsschemaserrorormessage) |  |
| ApiResponseDTODomainResponseDTO | [#/components/schemas/ApiResponseDTODomainResponseDTO](#componentsschemasapiresponsedtodomainresponsedto) |  |
| DomainResponseDTO | [#/components/schemas/DomainResponseDTO](#componentsschemasdomainresponsedto) | Response DTO for Domain |
| RecordRequestDTO | [#/components/schemas/RecordRequestDTO](#componentsschemasrecordrequestdto) | Request DTO for Record |
| ApiResponseDTORecordResponseDTO | [#/components/schemas/ApiResponseDTORecordResponseDTO](#componentsschemasapiresponsedtorecordresponsedto) |  |
| RecordResponseDTO | [#/components/schemas/RecordResponseDTO](#componentsschemasrecordresponsedto) | Response DTO for Record |
| ApiResponseDTOListDomainResponseDTO | [#/components/schemas/ApiResponseDTOListDomainResponseDTO](#componentsschemasapiresponsedtolistdomainresponsedto) |  |
| ApiResponseDTOListRecordResponseDTO | [#/components/schemas/ApiResponseDTOListRecordResponseDTO](#componentsschemasapiresponsedtolistrecordresponsedto) |  |
| ApiResponseDTODeleteResponseWithId | [#/components/schemas/ApiResponseDTODeleteResponseWithId](#componentsschemasapiresponsedtodeleteresponsewithid) |  |
| DeleteResponseWithId | [#/components/schemas/DeleteResponseWithId](#componentsschemasdeleteresponsewithid) |  |
| api token | [#/components/securitySchemes/api token](#componentssecurityschemesapi-token) |  |

## Path Details

***

### [GET]/zones

- Summary  
Get all domains

#### Responses

- 200 Returns all domains

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  // Response DTO for Domain
  result: {
    // Domain CFID
    id: string
    // Domain name(TLD)
    name: string
  }[]
}
```

- 404 Returns not found

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
  }
}
```

***

### [POST]/zones

- Summary  
Create domain

#### RequestBody

- application/json

```ts
// Request DTO for Domain
{
  // Domain name(TLD)
  name: string
}
```

#### Responses

- 200 Created domain

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  // Response DTO for Domain
  result: {
    // Domain CFID
    id: string
    // Domain name(TLD)
    name: string
  }
}
```

- 400 Bad request

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
  }
}
```

***

### [GET]/zones/{zone_id}/dns_records

- Summary  
Get all records

#### Responses

- 200 Return All Records

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  // Response DTO for Record
  result: {
    // Record ID
    id: string
    // Record type
    type: string
    // Record name
    name: string
    // Record content
    content: string
    // Zone(TLD) ID
    zoneId: string
    // Zone name(TLD)
    zoneName: string
    // Record creation time
    createdOn: string
    // Record modification time
    modifiedOn: string
    // Record priority
    priority?: integer
    // is proxyable: must false
    proxiable: boolean
    // is proxied: must false
    proxied: boolean
    // Record TTL
    ttl: integer
    // Record is locked: must false
    locked: boolean
    // Record comments
    comment?: string
  }[]
}
```

- 400 Bad request

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
  }
}
```

***

### [POST]/zones/{zone_id}/dns_records

- Summary  
Add Record by ID

#### RequestBody

- application/json

```ts
// Request DTO for Record
{
  // Record type
  type: string
  // Host name
  name: string
  // Record data
  content: string
  // TTL (Time to Live)
  ttl: integer
  // Priority
  priority?: integer
  // Proxied: cloudflare api compatibility
  proxied: boolean
  // comment
  comment: string
}
```

#### Responses

- 200 Return Record

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  // Response DTO for Record
  result: {
    // Record ID
    id: string
    // Record type
    type: string
    // Record name
    name: string
    // Record content
    content: string
    // Zone(TLD) ID
    zoneId: string
    // Zone name(TLD)
    zoneName: string
    // Record creation time
    createdOn: string
    // Record modification time
    modifiedOn: string
    // Record priority
    priority?: integer
    // is proxyable: must false
    proxiable: boolean
    // is proxied: must false
    proxied: boolean
    // Record TTL
    ttl: integer
    // Record is locked: must false
    locked: boolean
    // Record comments
    comment?: string
  }
}
```

- 400 Bad request

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
  }
}
```

***

### [GET]/zones/{zone_id}/dns_records/{dns_record_id}

- Summary  
Get Record by ID

#### Responses

- 200 Return Record

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  // Response DTO for Record
  result: {
    // Record ID
    id: string
    // Record type
    type: string
    // Record name
    name: string
    // Record content
    content: string
    // Zone(TLD) ID
    zoneId: string
    // Zone name(TLD)
    zoneName: string
    // Record creation time
    createdOn: string
    // Record modification time
    modifiedOn: string
    // Record priority
    priority?: integer
    // is proxyable: must false
    proxiable: boolean
    // is proxied: must false
    proxied: boolean
    // Record TTL
    ttl: integer
    // Record is locked: must false
    locked: boolean
    // Record comments
    comment?: string
  }
}
```

- 400 Bad request

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
  }
}
```

***

### [DELETE]/zones/{zone_id}/dns_records/{dns_record_id}

- Summary  
Remove Record by ID

#### Responses

- 200 Return Record

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
    id: string
  }
}
```

- 400 Bad request

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
  }
}
```

***

### [PUT]/zones/{zone_id}/dns_records/{dns_record_id}

- Summary  
Update Record by ID

#### RequestBody

- application/json

```ts
// Request DTO for Record
{
  // Record type
  type: string
  // Host name
  name: string
  // Record data
  content: string
  // TTL (Time to Live)
  ttl: integer
  // Priority
  priority?: integer
  // Proxied: cloudflare api compatibility
  proxied: boolean
  // comment
  comment: string
}
```

#### Responses

- 200 Return Record

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  // Response DTO for Record
  result: {
    // Record ID
    id: string
    // Record type
    type: string
    // Record name
    name: string
    // Record content
    content: string
    // Zone(TLD) ID
    zoneId: string
    // Zone name(TLD)
    zoneName: string
    // Record creation time
    createdOn: string
    // Record modification time
    modifiedOn: string
    // Record priority
    priority?: integer
    // is proxyable: must false
    proxiable: boolean
    // is proxied: must false
    proxied: boolean
    // Record TTL
    ttl: integer
    // Record is locked: must false
    locked: boolean
    // Record comments
    comment?: string
  }
}
```

- 400 Bad request

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
  }
}
```

***

### [GET]/zones/{cfid}

- Summary  
Get domain

#### Responses

- 200 Returns domain

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  // Response DTO for Domain
  result: {
    // Domain CFID
    id: string
    // Domain name(TLD)
    name: string
  }
}
```

- 404 Returns not found

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
  }
}
```

***

### [DELETE]/zones/{domain_id}

- Summary  
Delete domain

#### Responses

- 200 Deleted domain

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
    id: string
  }
}
```

- 400 Bad request

`application/json`

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
  }
}
```

## References

### #/components/schemas/DomainRequestDTO

```ts
// Request DTO for Domain
{
  // Domain name(TLD)
  name: string
}
```

### #/components/schemas/ApiResponseDTO

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
  }
}
```

### #/components/schemas/ErrorOrMessage

```ts
{
  code: integer
  message: string
}
```

### #/components/schemas/ApiResponseDTODomainResponseDTO

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  // Response DTO for Domain
  result: {
    // Domain CFID
    id: string
    // Domain name(TLD)
    name: string
  }
}
```

### #/components/schemas/DomainResponseDTO

```ts
// Response DTO for Domain
{
  // Domain CFID
  id: string
  // Domain name(TLD)
  name: string
}
```

### #/components/schemas/RecordRequestDTO

```ts
// Request DTO for Record
{
  // Record type
  type: string
  // Host name
  name: string
  // Record data
  content: string
  // TTL (Time to Live)
  ttl: integer
  // Priority
  priority?: integer
  // Proxied: cloudflare api compatibility
  proxied: boolean
  // comment
  comment: string
}
```

### #/components/schemas/ApiResponseDTORecordResponseDTO

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  // Response DTO for Record
  result: {
    // Record ID
    id: string
    // Record type
    type: string
    // Record name
    name: string
    // Record content
    content: string
    // Zone(TLD) ID
    zoneId: string
    // Zone name(TLD)
    zoneName: string
    // Record creation time
    createdOn: string
    // Record modification time
    modifiedOn: string
    // Record priority
    priority?: integer
    // is proxyable: must false
    proxiable: boolean
    // is proxied: must false
    proxied: boolean
    // Record TTL
    ttl: integer
    // Record is locked: must false
    locked: boolean
    // Record comments
    comment?: string
  }
}
```

### #/components/schemas/RecordResponseDTO

```ts
// Response DTO for Record
{
  // Record ID
  id: string
  // Record type
  type: string
  // Record name
  name: string
  // Record content
  content: string
  // Zone(TLD) ID
  zoneId: string
  // Zone name(TLD)
  zoneName: string
  // Record creation time
  createdOn: string
  // Record modification time
  modifiedOn: string
  // Record priority
  priority?: integer
  // is proxyable: must false
  proxiable: boolean
  // is proxied: must false
  proxied: boolean
  // Record TTL
  ttl: integer
  // Record is locked: must false
  locked: boolean
  // Record comments
  comment?: string
}
```

### #/components/schemas/ApiResponseDTOListDomainResponseDTO

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  // Response DTO for Domain
  result: {
    // Domain CFID
    id: string
    // Domain name(TLD)
    name: string
  }[]
}
```

### #/components/schemas/ApiResponseDTOListRecordResponseDTO

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  // Response DTO for Record
  result: {
    // Record ID
    id: string
    // Record type
    type: string
    // Record name
    name: string
    // Record content
    content: string
    // Zone(TLD) ID
    zoneId: string
    // Zone name(TLD)
    zoneName: string
    // Record creation time
    createdOn: string
    // Record modification time
    modifiedOn: string
    // Record priority
    priority?: integer
    // is proxyable: must false
    proxiable: boolean
    // is proxied: must false
    proxied: boolean
    // Record TTL
    ttl: integer
    // Record is locked: must false
    locked: boolean
    // Record comments
    comment?: string
  }[]
}
```

### #/components/schemas/ApiResponseDTODeleteResponseWithId

```ts
{
  success: boolean
  errors: {
    code: integer
    message: string
  }[]
  messages:#/components/schemas/ErrorOrMessage[]
  result: {
    id: string
  }
}
```

### #/components/schemas/DeleteResponseWithId

```ts
{
  id: string
}
```

### #/components/securitySchemes/api token

```ts
{
  "type": "http",
  "scheme": "bearer"
}
```
