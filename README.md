## Repository Overview

- This project is a development of a small set of [Backbase Service SDK](https://community.backbase.com/documentation/ServiceSDK/latest/index) (**Spring Boot** and **Cloud**) based Microservices projects that implement cloud-native intuitive, Microservices design patterns, and coding best practices.
- The project follows [**CloudNative**](https://www.cncf.io/) recommendations and the [**twelve-factor app**](https://12factor.net/) methodology for building *software-as-a-service apps* to show how μServices should be developed and deployed.
- This project uses technologies broadly used in Backbase.Like Docker, Kubernetes, Java SE 11, Spring Boot, Spring Cloud
- 'cards-presentation-service' has been generated using `core-service-archetype` - [Community guide](https://community.backbase.com/documentation/ServiceSDK/latest/create_a_core_service)
- This service implements logic for the UI components (Widget/Journey) to view and manage bank customer cards. Service integrates with [Marqeta](https://www.marqeta.com) card issuer.
- The service is implementation of `card-manager-client-api` - https://repo.backbase.com/specs/card-manager/
- Refer to [workflow guide](https://github.com/baas-devops-reference/docs/tree/master/backend) for Backend CI Workflow documentation

---
## Repository Description
### Project Structure
Explanation of the project structrue:
```
|- .github --> All GitHub Actions files
  |- ISSUE_TEMPLATE --> Templates for 'major','minor','patch' releases
  |- workflows --> GitHub Actions workflows for CI
|- src --> Source and Unit Test files
  |- main
    |- java/com/backbase/productled
      |- config
        |- MarqetaRestClientConfiguration.java --> Initializes and injects Marqeta beans using configuration properties
        |- UserManagerRestClientConfiguration.java --> Initializes and injects user-manager client
      |- controller
        |- CardsApiController.java --> Controller class to receive HTTP requests for card management services
        |- TravelNoticesApiController.java --> Controller class to receive HTTP requests for managing travel notices
      |- mapper
        |- CardsMappers.java --> Maps models between Marqeta <-> DBS
        |- MapperConstants.java --> Constants used by CardsMappers
      |- service
        |- CardsService.java - Service interacts with MarqetaService providing neccesary validations
        |- MarqetaService.java - Service executes Marquetas client methods and handles errors
        |- TravelNoticeService.java - Service handles logic related with Travel Notices and interacts with MarquetaService
        |- UserService.java - Service fetches user details using user-manager-api
      |- utils
        |- FrequencyEnum.java - Enum providing mapping of frequency values between DBS and Marqueta 
      |- Application.java - Initializer class for Cards Spring Boot Application
  |- test
    |- java/com/backbase/productled
      |- it
        |- CardsIT.java
        |- TravelNoticeIT.java
      |- service
        |- MarqetaServiceTest.java
        |- ApplicationTest.java
|- pom.xml
|- .gitignore
```
## Repository Configurations
### DSC (basic-installation.yml) configuration

```yaml
custom:
  enabled: true
  services:
    cards-presentation-service:
      enabled: true
      app:
        image:
          tag: "1.0.1-SNAPSHOT"
          repository: cards-presentation-service
      database: false
      livenessProbe:
        enabled: true
      readinessProbe:
        enabled: true
      env:
        BACKBASE_COMMUNICATION_HTTP_ACCESS-TOKEN-URI: "http://token-converter:8080/oauth/token"
        marqeta.username:
          valueFrom:
            secretKeyRef:
              name: marqeta-credentials
              key: username
        marqeta.password:
          valueFrom:
            secretKeyRef:
              name: marqeta-credentials
              key: password
        marqeta.baseUrl:
          valueFrom:
            configMapKeyRef:
              name: marqeta-env
              key: basePath
```
#### Marqeta config
The following properties **must** be set as they have no default:

Property | Description
--- | ---
**marqueta.baseUrl** | Base URL of Marqueta API (i.e. `https://sandbox-api.marqeta.com/v3`)
**marqueta.username** | API username
**marqueta.password** | API password

#### Stream config (if applicable)
Configuration properties used by Stream to interact with Backbase services (DBS, Token converter...).

Not applicable for 'cards-presentation-service'
---
## Customisation in project
CVV validation is not done as part this project due to testing complexity but can be added using marqeta getCvv api if required.
### Configuration changes
If project adopts Marqeta, only Marqeta configuration properties has to be changed.

### Component changes
If project adopts a different vendor than Marqeta, all services from `com.backbase.productled.service` package have to be replaced.

---
## Getting Started
### BaaS setup

- [ ] Step 1: Modify https://github.com/baas-devops-reference/ref-self-service/blob/main/self-service.tfvars by adding to `ecr` list name of new repository: `cards-presentation-service'
- [ ] Step 2: Checkout the following repository: https://github.com/baas-devops-reference/ref-applications-live/blob/main/runtimes/dev/basic-installation.yaml apply your deployment configurations example see _DSC (basic-installation.yml) configuration_ above.
- [ ] Step 3: Run the pre-commit to validate the configurations => ` pre-commit run --all-files --show-diff-on-failure --color=always`
- [ ] Step 4: Commit and Push your changes; wait for the template rendering and lint jobs to complete
- [ ] Step 5: Merge into `master` to trigger deployment.

### Local setup

- [ ] Step 1: Ensure to check the prerequisites for [local developer environment](https://community.backbase.com/documentation/ServiceSDK/latest/create_developer_environment)
- [ ] Step 2: Run command => `mvn spring-boot:run -Dspring.profiles.active=local`
- [ ] Step 3: To run the service from the built binaries, use => `java -jar target/cards-presentation-service-1.0.1-SNAPSHOT.jar -Dspring.profiles.active=local`

## Contributions
Please create a branch and a PR with your contributions. Commit messages should follow [semantic commit messages](https://seesparkbox.com/foundry/semantic_commit_messages)
