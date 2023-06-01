# Address API Spec

## Create Address

Method: POST

URL: /api/v1/contacts/{contactId}/addresses

Request Header:

- X-API-TOKEN : Token (Mandatory)

Request Body:

```json
{
  "street": "raha",
  "province": "Jaksel",
  "country": "wkwkw Land",
  "postalCode": "9956"
}
```

Response Body (Success):

```json
{
  "data": {
    "id": "random string",
    "street": "raha",
    "province": "Jaksel",
    "country": "wkwkw Land",
    "postalCode": "9956"
  }
}
```

Response Body (Failed):

```json
{
  "errors": "Contact is not found, ect..."
}
```

## List Address

Method: GET

URL: /api/v1/contacts/{contactId}/addresses

Request Header:

- X-API-TOKEN : Token (Mandatory)

Response Body (Success):

```json
{
  "data": [
    {
      "street": "raha",
      "province": "Jaksel",
      "country": "wkwkw Land",
      "postalCode": "9956"
    },
    {
      "street": "raha",
      "province": "Jaksel",
      "country": "wkwkw Land",
      "postalCode": "9956"
    }
  ]
}
```

Response Body (Failed):

```json
{
  "errors": "Contact is not found, ect..."
}
```

## Update Address

Method: PUT

URL: /api/v1/contacts/{contactId}/addresses/{addressId}

Request Header:

- X-API-TOKEN : Token (Mandatory)

Request Body:

```json
{
  "street": "raha",
  "province": "Jaksel",
  "country": "wkwkw Land",
  "postalCode": "9956"
}
```

Response Body (Success):

```json
{
  "data": {
    "id": "random string",
    "street": "raha",
    "province": "Jaksel",
    "country": "wkwkw Land",
    "postalCode": "9956"
  }
}
```

Response Body (Failed):

```json
{
  "errors": "Address is not found, ect..."
}
```

## Get Address

Method: POST

URL: /api/v1/contacts/{contactId}/addresses/{addressId}

Request Header:

- X-API-TOKEN : Token (Mandatory)

Response Body (Success):

```json
{
  "data": {
    "id": "random string",
    "firstName": "Otong",
    "lastName": "Surotong",
    "email": "otong@gmail.com",
    "phone": "088080802124"
  }
}
```

Response Body (Failed):

```json
{
  "errors": "Address is not found, ect..."
}
```

## Remove Address

Method: DELETE

URL: /api/v1/contacts/{contactId}/addresses/{addressId}

Request Header:

- X-API-TOKEN : Token (Mandatory)

Response Body (Success):

```json
{
  "data": "ok"
}
```

Response Body (Failed):

```json
{
  "errors": "Address is not found, ect..."
}
```