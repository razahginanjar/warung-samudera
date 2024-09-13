### Branch API


#### Create Branch

Request :

- Method : POST
- Endpoint : `/api/branch`
- Header :
    - Content-Type: application/json
    - Accept: application/json
- Body :

```json 
{
  "branchCode": "string",
  "branchName": "string",
  "address": "string",
  "phoneNumber": "string"
}
```

Response :

```json 
{
  "errors": "string",
  "data": {
    "branchId": "string",
    "branchCode": "string",
    "branchName": "string",
    "address": "string",
    "phoneNumber": "string"
  },
  "paging": null
}
```

#### Get Product

Request :

- Method : GET
- Endpoint : `/api/branch/{id_branch}`
- Header :
    - Accept: application/json

Response :

```json 
{
  "errors": "string",
  "data": {
    "branchId": "string",
    "branchCode": "string",
    "branchName": "string",
    "address": "string",
    "phoneNumber": "string"
  },
  "paging": null
}
```

#### Update Branch

Request :

- Method : PUT
- Endpoint : `/api/branch`
- Header :
    - Content-Type: application/json
    - Accept: application/json
- Body :

```json 
{
  "branchId": "string",
  "branchCode": "string",
  "branchName": "string",
  "address": "string",
  "phoneNumber": "string"
}
```

Response :

```json 
{
  "errors": "string",
  "data": {
    "branchId": "string",
    "branchCode": "string",
    "branchName": "string",
    "address": "string",
    "phoneNumber": "string"
  },
  "paging": null
}
```

#### Delete Branch

Request :

- Method : DELETE
- Endpoint : `/api/branch/{id_branch}`
- Header :
    - Accept: application/json
- Body :

Response :

```json 
{
  "errors": "string",
  "data": "OK",
  "paging": null
}
```