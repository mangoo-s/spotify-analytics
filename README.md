## Spotylitics

A Spring Boot backend that tracks and ranks music listening activity, using Redis Sorted Sets to power real-time track and artist leaderboards with sub-100ms response times through a multi-layer caching strategy. Built as a modular monolith (Spring Modulith) with JWT-based auth via Supabase, containerized with Docker.

## Tech Stack
- Backend Framework: Spring Boot
- Databases: PostgreSQL & Redis
- Authentication: Supabase Auth
- Security: Spring Security
- Monitoring: Prometheus, Grafana, and Micrometer
- Architecture: Event Driven Modular Monolith
