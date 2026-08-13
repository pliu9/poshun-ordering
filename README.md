## poshun-ordering

A small wholesale ordering backend I'm building as a personal project and portfolio piece while preparing for my next software engineering role.

The goal is to build it step by step, starting with the core ordering logic before adding persistence and an API.

Right now the project supports creating purchase orders with product lines, prices, and minimum order quantities.

## Modules
ordering-domain

Contains the core ordering model and business rules. It doesn't depend on Spring, JPA, or any other framework.

## Build

Java 21 and Maven 3.9 + are required.


A few decisions for the current implementation:

Money includes a currency.
The product price is stored on the order line when an order is created. Changing a product's price later shouldn't affect existing orders.
Minimum quantities are validated in the domain instead of the controller.
Persistence will be added separately so the domain classes don't need to depend on JPA.

What's next
Next I'll add an ordering-application module and implement the PlaceOrder flow with tests.
Database persistence and the HTTP API will come after that.
