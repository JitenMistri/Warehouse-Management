# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking

The first step is to define what a "cost" means and at what level it must be reported. I would gather the warehouse/store hierarchy, cost categories, accounting rules, allocation keys, billing periods, currency requirements and the source of truth for each cost.

Important considerations include direct vs indirect costs, shared warehouse overhead, labor, transportation, inventory carrying costs, utilities, rent and technology costs. A cost may also need to be allocated across multiple stores, products or business units. The allocation rule must be deterministic and auditable; otherwise two reports could produce different numbers from the same source data.

I would model immutable cost transactions with source identifiers, timestamps, cost category, amount/currency, warehouse/store/business-unit dimensions and an allocation method. Corrections should preferably create adjustment records rather than silently overwriting history.

Questions to clarify:
- What is the accounting source of truth?
- Are costs expected in real time or only daily/monthly?
- What allocation rules are approved by Finance?
- Do we need historical reporting after warehouse replacement?
- How are refunds, corrections and currency conversion handled?
- What level of auditability is required?

The main business value is reliable unit economics: management should be able to explain why a warehouse or store costs what it costs and identify the drivers behind changes.

## Scenario 2: Cost Optimization Strategies

I would first establish the baseline by cost category and business unit, then identify the largest and most controllable drivers. Typical opportunities could include warehouse utilization, labor scheduling, transportation consolidation, inventory positioning, energy usage and reducing manual operational work.

I would prioritize initiatives using expected savings, implementation effort, operational risk, customer/service impact and time to realize savings. For example, improving warehouse utilization may have a high benefit with relatively low operational risk, while changing a transport provider may have larger savings but require a longer validation period.

Each initiative should have a measurable baseline and target, such as cost per order, cost per unit shipped, warehouse utilization or transportation cost per shipment. I would run a pilot where possible, compare the result with the baseline, and only then roll the change out broadly.

Questions to clarify:
- Which costs are fixed vs variable?
- What service-level constraints cannot be compromised?
- Which initiatives already have Finance/Operations approval?
- How will savings be measured and attributed?
- Are there seasonal demand patterns?

The important point is to optimize total fulfillment cost, not one metric in isolation. A lower warehouse cost is not useful if it causes more transport cost, stockouts or slower delivery.

## Scenario 3: Integration with Financial Systems

Integration with financial systems creates a trusted bridge between operational events and accounting. It enables Finance to reconcile operational costs with invoices, general-ledger entries, budgets and actuals without manually re-entering data.

Before designing the integration I would identify the source systems, data ownership, identifiers, frequency, expected latency, error-handling process and reconciliation requirements. For real-time synchronization, events should have stable IDs so retries are idempotent and do not create duplicate financial transactions.

I would use an asynchronous integration pattern where appropriate: operational events can be published to a durable message broker, consumed by the financial integration service, validated, transformed and acknowledged only after successful processing. Failed messages should be retried and eventually routed to a dead-letter mechanism for investigation.

I would also provide reconciliation reports showing source totals versus financial-system totals, plus monitoring for delayed or failed messages.

Questions to clarify:
- Which system owns each financial field?
- Is exactly-once business processing required, or is idempotent at-least-once delivery acceptable?
- What is the maximum acceptable synchronization delay?
- What is the reconciliation frequency?
- What security and audit requirements apply to financial data?

## Scenario 4: Budgeting and Forecasting

Budgeting needs a consistent model of expected demand, capacity and cost drivers. I would gather historical order volumes, seasonality, warehouse capacity, staffing assumptions, transportation rates, supplier contracts, inflation assumptions and planned warehouse changes.

The system should separate assumptions from actuals. A budget version should be immutable once approved, while forecasts can be revised as new actual data arrives. I would support multiple scenarios such as baseline, optimistic and high-demand cases.

Forecasts should be explainable: instead of only showing a predicted amount, the system should show the main drivers behind the forecast. Examples are expected order volume, cost per shipment, labor hours and warehouse utilization.

Questions to clarify:
- What planning horizon is required?
- Who owns and approves assumptions?
- How frequently should forecasts be refreshed?
- Which cost drivers have reliable historical data?
- Do users need scenario comparison and variance analysis?

The key output is better resource allocation and earlier detection of cost overruns rather than simply producing another financial report.

## Scenario 5: Cost Control in Warehouse Replacement

Warehouse replacement needs special treatment because the Business Unit Code is reused while the physical warehouse is a new historical entity. The old warehouse should therefore be archived rather than overwritten. Its cost history must remain linked to the old warehouse record and its historical period.

I would capture the old warehouse's closing date, remaining stock, capacity, accumulated costs and relevant operational identifiers before creating the replacement. The replacement should get its own lifecycle timestamps while retaining the same Business Unit Code for business continuity.

For cost reporting, reports must distinguish the old and new warehouse records even though they share the same Business Unit Code. A warehouse version or immutable internal identifier is useful for this. Otherwise costs from two physical facilities could be incorrectly combined or one facility's history could overwrite another's.

Before replacement I would also compare the approved budget with transition costs: moving inventory, temporary storage, transport changes, setup costs and possible duplicate operating costs during the transition period.

Questions to clarify:
- Is the replacement in the same location?
- What costs belong to the old warehouse's closing period?
- What is the approved transition budget?
- Is there an overlap period where both facilities operate?
- How should reports display historical versus current costs for the reused Business Unit Code?

The core principle is that business identifiers can be reused for continuity, but financial history must use immutable internal records so that historical reporting remains correct.
