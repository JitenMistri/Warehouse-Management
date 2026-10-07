# Questions

## 1. Database access and manipulation

I would keep the separation around the Warehouse domain and make the persistence strategy consistent. Product and Store use Spring Data JPA repositories, while Warehouse uses the domain port `WarehouseStore` and a repository adapter. The port-based approach keeps the Warehouse business rules independent from the persistence implementation and makes unit testing straightforward.

For a maintained codebase, I would standardize on a layered/hexagonal approach: REST controllers handle transport concerns, application use cases handle business rules, and repositories handle persistence. This gives us clear transaction boundaries and avoids database concerns leaking into business logic. I would also avoid unnecessary `save()` calls on already-managed entities and let JPA dirty checking update them inside the transaction.

I would not refactor everything at once. I would first apply the pattern to new or frequently changed functionality, then migrate Product/Store incrementally when there is a business reason to touch those modules.

---

## 2. Generated OpenAPI endpoints vs manually coded endpoints

The original Warehouse API is defined by an OpenAPI contract. In the Spring Boot conversion I kept the YAML contract as documentation and implemented the controller explicitly instead of introducing framework-specific code generation.

A contract-first approach is useful because the API contract is explicit, can be reviewed independently by frontend/consumer teams, and reduces accidental differences between documentation and implementation. The trade-off is additional tooling and generated code that developers do not directly own.

For a larger externally consumed API I would use OpenAPI consistently, either through generated interfaces or a dedicated API contract module. For a small internal API, manually coded Spring controllers are also reasonable as long as the OpenAPI contract is kept in sync.

---

## 3. Testing strategy

I would prioritize tests in this order:

1. **Business-rule unit tests** – warehouse creation, replacement, archiving, location limits, capacity/stock rules and fulfillment constraints. These are fast and give the highest confidence for the core domain.
2. **API/integration tests** – happy paths and important error responses for each endpoint, including persistence behaviour.
3. **Persistence tests** – repository queries and transaction-sensitive behaviour where a unit test cannot provide enough confidence.
4. **External integration tests** – legacy/financial integrations, preferably with contract tests or reliable test doubles.

For every business rule I would keep at least one positive and one negative test. I would enforce an 80% line coverage threshold in CI with JaCoCo, but I would not treat coverage alone as quality. Pull requests should add tests for new branches and business rules, and CI should run the complete test suite before merge.

For the Store requirement specifically, I would test both commit and rollback scenarios to prove that the legacy call happens only after a successful transaction. In a larger system I would consider a transactional outbox/event pattern for reliable delivery instead of making a synchronous external call from the transaction callback.
