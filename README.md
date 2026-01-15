# Ultimate Calendar Maven

API for managing tenants, staff, services, appointments, and availability in a calendar system.

## API Documentation

The complete API specification is available in OpenAPI 3.0 format in the `openapi.yaml` file at the root of this project.

### Importing to Postman

To import the API specification directly into Postman:

1. Open Postman
2. Click on **Import** button (top left)
3. Select **File** tab
4. Choose the `openapi.yaml` file from this repository
5. Click **Import**

Postman will automatically create a collection with all the API endpoints, including:
- Request parameters
- Request bodies with examples
- Response schemas
- All HTTP methods (GET, POST, PUT, PATCH, DELETE)

### API Endpoints Overview

The API is organized into the following main resources:

- **Tenants**: Multi-tenant organization management
- **Customers**: Customer records (tenant-scoped)
- **Services**: Service catalog with pricing and duration
- **Staff**: Staff member management
- **Appointments**: Appointment booking and management
- **Availability**: Check available time slots
- **Staff Assignments**: Assign services to staff members
- **Staff Schedules**: Manage staff schedules
- **Working Hours**: Define staff working hours by day
- **Time Off**: Manage staff time off periods

### Base URL

- Local development: `http://localhost:8080`
- Production: Update the server URL in `openapi.yaml` as needed

### Technologies

- Java 21
- Spring Boot 3.5.6
- PostgreSQL
- Maven

## Building and Running

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

## License

See LICENSE file for details.
