### Transaction API


#### Create Transaction

Request :

- Method : POST
- Endpoint : `/api/transactions`
- Header :
    - Content-Type: application/json
    - Accept: application/json
- Body :

Request :

Transaction Type :

- "1" : `EAT_IN`
- "2" : `ONLINE`
- "3" : `TAKE_AWAY`

```json
{
  "transactionType": "string",
  "billDetails": [
    {
      "productId": "string",
      "quantity": "number"
    },
    {
      "productId": "string",
      "quantity": "number"
    }
  ]
}
```

Response :

- Template Receipt Number : `{branchCode}-{year}-{sequenceNumber}`
- Transaction Type : `EAT_IN | TAKE_AWAY | ONLINE`

```json
{
  "errors": "string",
  "data": {
    "customerName": "String",
    "billId": "string",
    "receiptNumber": "string",
    "transDate": "localDateTime",
    "transactionType": "string",
    "billDetails": [
      {
        "billDetailId": "string",
        "billId": "string",
        "product": {
          "productId": "string",
          "productPriceId": "string",
          "productCode": "string",
          "productName": "string",
          "price": "bigDecimal",
          "branch": {
            "branchId": "string",
            "branchCode": "string",
            "branchName": "string",
            "address": "string",
            "phoneNumber": "string"
          }
        },
        "quantity": "number",
        "totalSales": "bigDecimal"
      }
    ]
  },
  "paging": null
}
```

#### Get Transaction

Request :

- Method : GET
- Endpoint : `/api/transactions/{id_bill}`
- Header :
    - Accept: application/json
- Body :

Response :

- Template Receipt Number : `{branchCode}-{year}-{sequenceNumber}`
- Transaction Type : `EAT_IN | TAKE_AWAY | ONLINE`

```json
{
  "errors": "string",
  "data": {
    "billId": "string",
    "receiptNumber": "string",
    "transDate": "localDateTime",
    "transactionType": "string",
    "billDetails": [
      {
        "billDetailId": "string",
        "billId": "string",
        "product": {
          "productId": "string",
          "productCode": "string",
          "productName": "string",
          "price": "bigDecimal",
          "branch": {
            "branchId": "string",
            "branchCode": "string",
            "branchName": "string",
            "address": "string",
            "phoneNumber": "string"
          }
        },
        "quantity": "number"
      }
    ]
  },
  "paging": null
}
```

#### List Transaction

Pattern string date : `dd-MM-yyyy`

Request :

- Method : GET
- Endpoint : `/api/transactions`
- Header :
    - Accept : application/json
- Query Param :
    - page : number
    - size : number
    - receiptNumber : string `optional`
    - startDate : string `optional`
    - endDate : string `optional`
    - transType : string `optional`
    - productName : string `optional`
- Body :

Response :

- Template Receipt Number : `{branchId}-{year}-{sequenceNumber}`
- Transaction Type : `EAT_IN | TAKE_AWAY | ONLINE`

```json
{
  "errors": "string",
  "data": [
    {
      "billId": "string",
      "receiptNumber": "string",
      "transDate": "localDateTime",
      "transactionType": "string",
      "billDetails": [
        {
          "billDetailId": "string",
          "billId": "string",
          "product": {
            "productId": "string",
            "productCode": "string",
            "productName": "string",
            "price": "bigDecimal",
            "branch": {
              "branchId": "string",
              "branchCode": "string",
              "branchName": "string",
              "address": "string",
              "phoneNumber": "string"
            }
          },
          "quantity": "number"
        }
      ]
    }
  ],
  "paging": null
}
```

#### Get Total Sales

Request :

Pattern string date : `dd-MM-yyyy`

- Method : GET
- Endpoint : `/api/transactions/total-sales`
- Header :
    - Accept: application/json
- Query Param :
    - startDate : string `optional`,
    - endDate : string `optional`,
- Body :

Response :

```json
{
  "errors": "string",
  "data": {
    "eatIn": "bigDecimal",
    "takeAway": "bigDecimal",
    "online": "bigDecimal"
  },
  "paging": null
}
```