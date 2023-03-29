 [![Java CI with Maven](https://github.com/alwayswanna/fellow-worker/actions/workflows/maven.yml/badge.svg)](https://github.com/alwayswanna/fellow-worker/actions/workflows/maven.yml)
 
 
 fellow-worker
=================

1. api-common
2. client-manager
3. fellow-worker-service
4. oauth2-persistence
5. oauth2-server

## _api-common_

Shared module for multiple services, contains classes and models for API & Swagger.

## _client-manager_

Client management module. Custom metrics are implemented for the user data management service. This service is 
designed to create, delete, modify accounts.
SwaggerUI uri:

http://127.0.0.1:8090/swagger-ui.html

## _fellow-worker-service_

Vacancies & resume management module. This service is designed to create, delete and modify resumes or vacancies.
SwaggerUI uri:

http://127.0.0.1:4334/swagger-ui.html

## _oauth2-persistence_

Shared module for multiple services, contains classes and models for persistence (clients).

## _oauth2-server_

Server which create JWT tokens.

http://127.0.0.1:9001/authorize/token

## _cv-generator_

Service which cat create PDF files with resume info.

http://127.0.0.1:7044/swagger-ui.html

## _fellowworkerfront_

Frontend module for all microservices.

http://127.0.0.1:8888/


Final qualifying work for the university. Application for creating, editing and deleting resumes or vacancies. 
Job search site. For authorization, the OAuth2 standard is used. When creating a resume, a PDF document is created,
which the user can download. 
 - Flutter is used to display user data.
 - JVM, Spring Boot is used for realize backend-logic.

<img alt="Application architecture" src="/assets/application-architecture.png" title="Application architecture"/>

All microservices build to Docker images, created docker-compose manifest for demo.

Main page of user view service:
<img alt="Application architecture" src="/assets/frontend-main-view.png" title="Main view"/>

For monitoring is used stack Prometheus & Grafana:

<img alt="Application architecture" src="/assets/monitoring-dashboards.png" title="Dashboards"/>
