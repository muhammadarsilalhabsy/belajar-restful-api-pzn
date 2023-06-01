# User API spec

## Register User

Method: POST

URL : /api/v1/users/register

Request Body:

```json
{
  "name": "Otong Surotong",
  "username": "otong",
  "password": "otong123"
}
```

Response Body (Success):

```json
{
  "data": "OK"
}
```

Response Body (Failure):

```json
{
  "errors": "Username is already exists, Username must not blank, ect"
}
```

## Login User

Method: POST

URL : /api/v1/auth/login

Request Body:

```json
{
  "username": "otong",
  "password": "otong123"
}
```

Response Body (Success):

```json
{
  "data": {
    "token": "UUID123-456-78910",
    "expiredAt": 4000332 // milisseconds
  }
}
```

Response Body (Failed, 401):

```json
{
  "data": "KO",
  "errors": "Username must not blank, Wrong Username or password ect"
}
```

## Get User

Method: GET

URL : /api/v1/users/current

Request Header:

- X-API-TOKEN : Token (Mandatory)

Response Body (Success):

```json
{
  "data": {
    "username": "otong",
    "name": "Otong Surotong"
  }
}
```

Response Body (Failed, 401):

```json
{
  "errors": "Unauthorized"
}
```

## Update User

Method: PATCH

URL : /api/v1/users/current

Request Header:

- X-API-TOKEN : Token (Mandatory)

Response Body (Success):

```json
{
  "password": "otong1098",
  "name": "Otong Surotong"
}
```

Response Body (Failed, 401):

```json
{
  "errors": "Unauthorized"
}
```

## Logout User

Method: DELETE

URL : api/v1/auth/logout

Request Header:

- X-API-TOKEN : Token (Mandatory)

Response Body (Success):

```json
{
  "data": "ok"
}
```