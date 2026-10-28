# Part 1 — REST API (Server)

Standalone Spring Boot REST API for managing cities, passengers, airports, and aircraft. It uses Spring Data JPA and an H2 relational database stored in `./data` by default. The `data/` directory is ignored by Git.

## CLI repository

The CLI remains an independent GitHub repository and is linked here as a Git submodule at `logan_sam_sdat_midterm_cli`, tracking its `feature/cli-implementation` branch. Clone the API repository and its CLI checkout together with:

```powershell
git clone --recurse-submodules <api-repository-url>
```

If you already cloned the API repository without submodules, initialize them with:

```powershell
git submodule update --init --recursive
```

For VS Code, open `Sam-and-Logan-Midterm.code-workspace` at the repository root. It opens the API and CLI as separate workspace folders so the Java extension imports each Maven project and recognizes each project's standard `src/main/java` source root.

Commit CLI changes in the CLI repository first. Then commit the updated submodule pointer in the API repository to record which CLI revision goes with that API revision.

## Requirements and running locally

- Java 17+
- Maven 3.8+

Start the API from this directory:

```powershell
mvn spring-boot:run
```

The API listens on `http://localhost:8080`. Run the integration tests with:

```powershell
mvn test
```

The included [Postman collection](./postman/Part-1-REST-API.postman_collection.json) can be imported into Postman. It has example requests for all four entities and the paging/sorting endpoint.

## Entity relationships

- A city has many airports; every airport belongs to one city.
- A passenger lives in one city and can be linked to many aircraft.
- An aircraft can be linked to many passengers and many airports.

Create/update requests use related record IDs. For example, an airport needs a `cityId`; `aircraftIds` may be an empty array. Full `PUT` requests replace the entity's linked IDs with the supplied arrays.

## Endpoints

Each collection supports `GET` and `POST`; each item supports `GET`, `PUT`, and `DELETE`.

| Entity | Collection | Item |
|---|---|---|
| Cities | `/api/cities` | `/api/cities/{id}` |
| Airports | `/api/airports` | `/api/airports/{id}` |
| Passengers | `/api/passengers` | `/api/passengers/{id}` |
| Aircraft | `/api/aircraft` | `/api/aircraft/{id}` |

`POST` returns `201 Created`, successful `DELETE` returns `204 No Content`, missing records return `404`, invalid requests return `400`, and deletes/updates that violate a database relationship or unique constraint return `409 Conflict`.

### Example request bodies

```json
// POST /api/cities
{"name":"Seattle","state":"Washington","population":755000}
```

```json
// POST /api/airports
{"name":"Seattle-Tacoma International","code":"SEA","cityId":1,"aircraftIds":[]}
```

```json
// POST /api/passengers
{"firstName":"Jordan","lastName":"Lee","phoneNumber":"555-0101","cityId":1,"aircraftIds":[]}
```

```json
// POST /api/aircraft
{"type":"737","airlineName":"Example Air","numberOfPassengers":180,"passengerIds":[1],"airportIds":[1]}
```

## Pagination and sorting (JPA `Pageable`)

`GET /api/cities` accepts Spring Data's pageable parameters:

```text
GET http://localhost:8080/api/cities?page=0&size=5&sort=population,desc
```

- `page` is zero-based (first page is `0`).
- `size` sets the number of records per page.
- `sort` accepts a property and optional direction, e.g. `sort=name,asc` or `sort=population,desc`. Multiple `sort` parameters can be supplied.
- Defaults are `page=0`, `size=10`, sorted by `name` ascending.

The response contains `content`, `totalElements`, `totalPages`, `number`, `size`, `first`, and `last`. Pagination keeps large collections from requiring one unbounded response. Spring Data JPA translates the requested page, page size, and ordering into the database query.

## Database

The default H2 database is relational and file-backed for easy local use. Its console is available at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:file:./data/sprint-api`, username `sa`, blank password). Hibernate creates/updates the schema from the entity mappings. Tests use an isolated in-memory H2 database.

## GitHub collaboration setup

Keep this server in a repository separate from the CLI. This workspace has no GitHub remote configured and the available GitHub integration cannot create repositories, pull requests, or project boards. After creating a new repository on GitHub, push this project there and use feature branches (for example `feature/city-api`), open pull requests against `main`, and require the included Java CI check to pass before merging.

Create a GitHub Project board for the sprint and link it to the API repository. Add work items for the four entity APIs, relationship handling, pagination/sorting, and API testing; move items through a shared workflow such as **Backlog → In progress → In review → Done**, assigning each to a teammate and linking implementation pull requests. The included pull-request template asks contributors to link the related issue and report tests.
