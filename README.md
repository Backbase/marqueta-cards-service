## Repository Overview

- This project is a development of a small set of [Backbase Service SDK](https://community.backbase.com/documentation/ServiceSDK/latest/index) (**Spring Boot** and **Cloud**) based Microservices projects that implement cloud-native intuitive, Microservices design patterns, and coding best practices.
- The project follows [**CloudNative**](https://www.cncf.io/) recommendations and the [**twelve-factor app**](https://12factor.net/) methodology for building *software-as-a-service apps* to show how μServices should be developed and deployed.
- This project uses technologies broadly used in Backbase.Like Docker, Kubernetes, Java SE 11, Spring Boot, Spring Cloud
- 'marqueta-cards-service' has been generated using `core-service-archetype` - [Community guide](https://community.backbase.com/documentation/ServiceSDK/latest/create_a_core_service)
- This service implements logic for the UI components (Widget/Journey) to view and manage bank customer cards. Service integrates with [Marqeta](https://www.marqeta.com) card issuer.
- The service is implementation of `card-manager-client-api` - https://repo.backbase.com/specs/card-manager/
- Refer to [workflow guide](../../../docs/tree/master/backend) for Backend CI Workflow documentation

---
## Repository Description
### Project Structure
The project structure for each custom integration service follows the pattern as described below :

```
.
├── .github                       # All GitHub Actions files
│   ├── ISSUE_TEMPLATE            # Templates for 'major','minor','patch' releases
│   └── workflows                 # GitHub Actions workflows for CI
├── src                           # Source and Unit Test files
    ├── main                      # Application container projects
    │   ├── java/com/backbase/productled
    │   │   ├── api               # Controller classes
    │   │   │   └── ...
    │   │   ├── config            # Configuration classes
    │   │   │   └── ...
    │   │   ├── mapper            # Model classes
    │   │   │   └── ...
    │   │   └── service           # Service classes
    │   │       └── ...
    │   └── resources             # All resource files except core classes
    │       └── ...
    └── test                      # JUnit test file
        └── ...
```

To view individual classes for this repository, select relevant branch from the GitHub UI and then press ‘.'
This will open the GitHub Web Editor.Alternatively, you can also access the Web Editor by changing .com to .dev in the URL.

Expand each file in the Web Editor for explanation and purpose.

## Repository Configurations
### DSC (basic-installation.yml) configuration

```yaml

      env:
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

Not applicable for 'marqueta-cards-service'
---
## Customisation in project
CVV validation is not done as part this project due to testing complexity but can be added using marqeta getCvv api if required.
### Configuration changes
If project adopts Marqeta, only Marqeta configuration properties has to be changed.

### Component changes
If project adopts a different vendor than Marqeta, all services from `com.backbase.productled.service` package have to be replaced.

---
## Getting Started


### Local setup

- [ ] Step 1: Ensure to check the prerequisites for [local developer environment](https://community.backbase.com/documentation/ServiceSDK/latest/create_developer_environment)
- [ ] Step 2: Run command => `mvn spring-boot:run -Dspring.profiles.active=local`
- [ ] Step 3: To run the service from the built binaries, use => `java -jar target/marqueta-cards-service-1.0.1-SNAPSHOT.jar -Dspring.profiles.active=local`

## Contributions
Please create a branch and a PR with your contributions. Commit messages should follow [semantic commit messages](https://seesparkbox.com/foundry/semantic_commit_messages)
