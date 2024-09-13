### Product API


#### Create Product

Request :

- Method : POST
- Endpoint : `/api/products`
- Header :
    - Content-Type: application/json
    - Accept: application/json
- Body :

```json 
{
  "productCode": "string",
  "productName": "string",
  "price": "big decimal",
  "branchId": "string"
}
```

Response :

```json 
{
  "errors": "string",
  "data": {
    "productId": "string",
    "productPriceId": "string",
    "productCode": "string",
    "productName": "string",
    "price": "big decimal",
    "branch": {
      "branchId": "string",
      "branchCode": "string",
      "branchName": "string",
      "address": "string",
      "phoneNumber": "string"
    }
  },
  "paging": null
}
```

#### List Product

Request :

- Method : GET
- Endpoint : `/api/products`
- Header :
    - Accept: application/json
- Query Param :
    - size : number,
    - page : number,
    - productCode : string `optional`,
    - productName : string `optional`,
    - minPrice : string `optional`,
    - maxPrice : string `optional`,

Response :

```json 
{
  "errors": "string",
  "data": [
    {
      "productId": "string",
      "productPriceId": "string",
      "productCode": "string",
      "productName": "string",
      "price": "big decimal",
      "branch": {
        "branchId": "string",
        "branchCode": "string",
        "branchName": "string",
        "address": "string",
        "phoneNumber": "string"
      }
    },
    {
      "productId": "string",
      "productPriceId": "string",
      "productCode": "string",
      "productName": "string",
      "price": "big decimal",
      "branch": {
        "branchId": "string",
        "branchCode": "string",
        "branchName": "string",
        "address": "string",
        "phoneNumber": "string"
      }
    }
  ],
  "paging": {
    "count": "number",
    "totalPage": "number",
    "page": "number",
    "size": "number"
  }
}
```

#### List Product By Branch Id

Request :

- Method : GET
- Endpoint : `/api/products/{id_branch}`
- Header :
    - Accept: application/json

Response :

```json 
{
  "errors": "string",
  "data": [
    {
      "productId": "string",
      "productPriceId": "string",
      "productCode": "string",
      "productName": "string",
      "price": "big decimal",
      "branch": {
        "branchId": "string",
        "branchCode": "string",
        "branchName": "string",
        "address": "string",
        "phoneNumber": "string"
      }
    },
    {
      "productId": "string",
      "productPriceId": "string",
      "productCode": "string",
      "productName": "string",
      "price": "big decimal",
      "branch": {
        "branchId": "string",
        "branchCode": "string",
        "branchName": "string",
        "address": "string",
        "phoneNumber": "string"
      }
    }
  ],
  "paging": {
    "count": "number",
    "totalPage": "number",
    "page": "number",
    "size": "number"
  }
}
```

#### Update Product

Request :

- Method : PUT
- Endpoint : `/api/products`
- Header :
    - Content-Type: application/json
    - Accept: application/json
- Body :

```json 
{
  "productId": "string",
  "productCode": "string",
  "productName": "string",
  "price": "big decimal",
  "branchId": "string"
}
```

Response :

```json 
{
  "errors": "string",
  "data": {
    "productId": "string",
    "productPriceId": "string",
    "productCode": "string",
    "productName": "string",
    "price": "big decimal",
    "branch": {
      "branchId": "string",
      "branchCode": "string",
      "branchName": "string",
      "address": "string",
      "phoneNumber": "string"
    }
  },
  "paging": null
}
```

#### Delete Product

Request :

- Method : DELETE
- Endpoint : `/api/products/{id_product}`
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
