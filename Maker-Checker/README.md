# Maker-Checker Banking

This existing Spring Boot application now uses two generic, in-process engines:

- RBC authorization evaluates persisted rules for an application, user, action, resource, and context.
- Audit Trail stores generic events, actor IDs, resources, actions, timestamps, and metadata.

## Run

Configure MySQL in `src/main/resources/application.properties`, then run:

```text
mvn spring-boot:run
```

The API runs on port `8000`. Hibernate creates the `rbc_rules` and `audit_events` tables alongside the existing banking tables. Demo rules are inserted on first startup for maker IDs `1`, `2`, `3` and checker IDs `201`, `202`, `203`.

## Authorization APIs

`POST /rbc/check` accepts a generic request such as:

```json
{
  "applicationKey": "MAKER_CHECKER",
  "userId": 1,
  "action": "INITIATE_TRANSFER",
  "resource": "BANKING_RESOURCE",
  "context": {"amount": 4000}
}
```

Rules can be inspected with `GET /rbc/rules` and added dynamically with `POST /rbc/rules`. Conditions support `EQUALS`, `NOT_EQUALS`, numeric comparisons, and `IN`; conditions in one rule are combined with AND.

Transfer creation and rejected-transfer resubmission check maker rules. Pending lists, approvals, and rejections check checker rules. The existing maker/checker APIs remain unchanged.

## Audit APIs

`POST /audit/events` records a generic event. History is available from `GET /audit/events`, or filtered by application, actor, or resource. Banking flows record `BENEFICIARY_CREATED`, `TRANSFER_INITIATED`, `TRANSFER_APPROVED`, and `TRANSFER_REJECTED` events; those event names belong to this application, not the audit engine.