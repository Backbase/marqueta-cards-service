# cards-presentation-service

Generated using `core-service-archetype` - Community
guide: https://community.backbase.com/documentation/ServiceSDK/11-3-0/create_a_core_service

This service is an implementation of `cards-presenation-spec`
- https://stash.backbase.com/projects/CARDS/repos/cards-presentation-spec

This service is an Implementation of Cards Presentation to communicate with Mambu and Marqeta to retrieve cards
information

## Getting started

Components overview:

- Controller/API - `CardsApiController`: Controller class to receive HTTP requests from `acount-statement-service` and
  post to Mambu using the restful API - DocumentsApi. Implements controller interface from spec - `AccountStatementsApi`
  .
- Configuration - `MambuConfiguration`, `MarqetaConfiguration`, `MarqetaConfigurationProperties`
  & `MambuConfigurationProperties`: configuration classes to load properties from config file and initialize Mambu
  client.
- Mapper - `CardMapper`: Map models from Mambu <-> DBS.
- Service - `CardsService`: Orchestrate the business to Mambu and map response to DBS model.

## Configuration

Service configuration is under `src/main/resources/application.yml`.

### basic-installation.yml config

```
custom:
  enabled: true
  services:
    cards-presentation-service:
      enabled: true
      app:
        image:
          tag: "1.0.0-SNAPSHOT"
          repository: cards-presentation-service
      database: false
      livenessProbe:
        enabled: false
      readinessProbe:
        enabled: false
      env:
        BACKBASE_COMMUNICATION_HTTP_ACCESS-TOKEN-URI: "http://token-converter:8080/oauth/token"
        mambu.username:
          valueFrom:
            secretKeyRef:
              name: mambu-credentials
              key: username
        mambu.password:
          valueFrom:
            secretKeyRef:
              name: mambu-credentials
              key: password
        mambu.baseUrl:
          valueFrom:
            configMapKeyRef:
              name: mambu-env
              key: basePath
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

## Running

To run in Baas - WU cluster:

- Request for ECR creation, see this
  guide: https://backbase.atlassian.net/wiki/spaces/BAAS/pages/1781531084/3.+Create+docker+repo+ECR

- Checkout the following repository: https://github.com/BackbaseServices/baas-wu-applications-live apply your deployment
  configurations example see _Configuration_ above.

- Run the pre-commit to validate the configurations:

` pre-commit run --all-files --show-diff-on-failure --color=always`

- Commit and Push your changes; wait for the template rendering and lint jobs to complete

- Merge into `master` to trigger deployment.

To run the service in development mode, use:

- `mvn spring-boot:run`

To run the service from the built binaries, use:

- `java -jar target/cards-presentation-service-1.0.0-SNAPSHOT.jar`

#### JWT config

Property | Description | Default
--- | --- | ---
**
sso.jwt.internal.signature.key.type** | https://community.backbase.com/documentation/ServiceSDK/latest/jwt_key_configuration | `ENV`
**
sso.jwt.internal.signature.key.value** | https://community.backbase.com/documentation/ServiceSDK/latest/jwt_key_configuration | `SIG_SECRET_KEY`

#### Mambu config

The following properties **must** be set as they have no default:

Property | Description
--- | ---
**mambu.baseUrl** | Base URL of Mambu API (i.e. `https://backbase.sandbox.mambu.com/api`)
**mambu.username** | API username
**mambu.password** | API password