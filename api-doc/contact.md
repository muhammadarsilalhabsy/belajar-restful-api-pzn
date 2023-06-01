# Contact API Spec

## Create Contact

Method: POST

URL: /api/v1/contacts

Request Header:

- X-API-TOKEN : Token (Mandatory)

Request Body:

```json
{
  "firstName": "Otong",
  "lastName": "Surotong",
  "email": "otong@gmail.com",
  "phone": "088080802124"
}
```

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
  "errors": "Email format is invalid, ect..."
}
```

## Update Contact

Method: PUT

URL: /api/v1/contacts/{idContact

Request Header:

- X-API-TOKEN : Token (Mandatory)

Request Body:

```json
{
  "firstName": "Otong",
  "lastName": "Surotong",
  "email": "otong@gmail.com",
  "phone": "088080802124"
}
```

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
  "errors": "Email format is invalid, ect..."
}
```

## Get Contact

Method : GET

URL: /api/v1/contacts/{idContact}

Request Header:

- X-API-TOKEN : Token (Mandatory)

Response Body (Success):

```json
{
  "data": 
    {
      "id": "random string",
      "firstName": "Otong",
      "lastName": "Surotong",
      "email": "otong@gmail.com",
      "phone": "088080802124"
    }
}
```

Response Body (Failed, 404):

```json
{
  "errors": "Contact not found"
}
```

## Search Contact

Method : GET

URL: /api/v1/contacts

Request Param:

- name : `String`, contact firstName or lastName, `using query like` (optional)
- phone : `String`, contact phone, `using query like` (optional)
- email : `String`, contact email, `using query like` (optional)
- page : `Integer` start from 0, default 0
- size : `Integer` default 10

Request Header:

- X-API-TOKEN : Token (Mandatory)

Response Body (Success):

```json
{
  "data":[
    {
      "id": "random string",
      "firstName": "Otong",
      "lastName": "Surotong",
      "email": "otong@gmail.com",
      "phone": "088080802124"
    },
    {
      "id": "random string",
      "firstName": "jamal",
      "lastName": "ludin",
      "email": "jamal@gmail.com",
      "phone": "09090911"
    }
  ],
  "paging": {
    "currentPage":0,
    "totalPages":10,
    "size":10
  }
    
}
```

Response Body (Failed):

```json
{
  "errors": "Unauthorized"
}
```

## Remove Contact

Method : DELETE

URL: /api/v1/contacts/{idContact}

Request Header:

- X-API-TOKEN : Token (Mandatory)

Response Body (Success):

```json
{
  "data": "OK"
}
```

Response Body (Failed):
```json
{
  "errors": "Contact not found"
}
```