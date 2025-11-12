# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Venn is a self-hosted billing platform built using **Polylith architecture** in Clojure. The project consists of two repositories:

- **venn** (this repo): Core billing platform with Polylith architecture and PRIMARY PROJECT MANAGEMENT repository
- **venn-development**: Development and testing repository used for local development and integration

Note: ALL project management, issues, and primary development should be conducted in the **venn** repository. The **venn-development** repository is strictly for local development and testing purposes.

The core platform follows a component-based, modular design pattern with clear separation between components, bases, and projects.

## Architecture

**Polylith Structure:**
- **Components** (`/components/`): Reusable business logic modules (customer, database, http, etc.)
- **Bases** (`/bases/`): Applications that compose components (agent, server)
- **Projects** (`/projects/`): Deployable artifacts

**Key Systems:**
- **Agent**: SQLite-based billing service for lightweight deployments
- **Server**: PostgreSQL-based main billing server
- **Development**: A monolith service used in development and testing
- **System Management**: Integrant + Aero for dependency injection and configuration
- **HTTP**: Undertow server with Ring handlers and component-based routing and data driven routes with reitit
- **Schema Validation**: Defined using malli schemas

## Development Commands

**Start Development Environment:**
```bash
bb dev        # Start development REPL with all components loaded
bb test       # Start test environment and REPL
```

**Database Access:**
```bash
bb psql-dev   # Connect to development PostgreSQL database
bb psql-test  # Connect to test PostgreSQL database
```

**REPL Development:**
```bash
clj -M:dev:repl    # Development REPL
```

**Build Commands:**
```bash
clj -T:build uberjar :project server    # Build server uberjar
clj -T:build uberjar :project agent     # Build agent uberjar
```

**Testing:**
```bash
clj -M:poly test       # Run tests for all projects
```

**Polylith Commands:**
```bash
clj -M:poly info       # Show workspace info and project structure
clj -M:poly deps       # Show dependencies between components
clj -M:poly check      # Check for circular dependencies and issues
clj -M:poly diff       # Show what has changed since last tag
clj -M:poly create component name:my-component    # Create new component
clj -M:poly create base name:my-base              # Create new base
clj -M:poly create project name:my-project        # Create new project
```

## Development Workflow

**REPL-Driven Development:**
- Use `/development/src/dev/venn.clj` for system management
- System lifecycle: `(go)` to start, `(halt)` to stop, `(reset)` to restart
- Database migration helpers available for both SQLite and PostgreSQL

**Component Interface Pattern:**
- Each component exposes a single `interface.clj` namespace
- Internal implementation in separate namespaces (handlers.clj, core.clj, etc.)
- Always use the interface namespace when depending on other components

## Configuration

**Environment Profiles:**
- `:development` - Local PostgreSQL + SQLite, hot reloading enabled
- `:test` - PostgreSQL on port 5433, in-memory SQLite
- `:prod` - Environment-specific database configuration

**Configuration Files:**
- System configuration in `resources/system.edn` with Aero profiles
- Component-specific resources in `components/<name>/resources/`
- Default system schema with Malli validation

## Database Management

**Databases:**
- **Server**: PostgreSQL with connection pooling
- **Agent**: SQLite (file-based for dev, in-memory for tests)
- **Migrations**: Migratus with separate migration directories per service

**Migration Locations:**
- Server: `bases/server/resources/server/migrations/`
- Agent: `bases/agent/resources/agent/migrations/`

## Code Patterns

**Handler Organization:**
- HTTP handlers separated from business logic
- Domain models define schemas and business rules
- Consistent response formatting with proper HTTP status codes

**Route Structure:**
- Component-based route organization
- OpenAPI-compatible route definitions
- Routes combined in monolithic mode during development

**Error Handling:**
- Consistent error response format
- Proper HTTP status code mapping
- Component-level error boundaries

## GitHub Issue Guidelines

**Work Item Creation:**
- When creating GitHub issues, do NOT include "Add documentation" or "Update documentation" as work items
- Focus on functional implementation tasks only
- Documentation updates should happen naturally as part of code changes
- Keep issue titles succinct and action-oriented (e.g., "Add new defhandler macro for easier handler generation")
- All issues must be tied to an epic (use "Maintenance Epic" as catch-all for non-project work)
- All issues must be assigned to a milestone

**Required Labels:**
- **System Label**: Must apply one or more of: `agent`, `server`, or `dev:venn`
  - `agent` - Changes affecting the Venn Agent (SQLite-based service)
  - `server` - Changes affecting the Venn Server (PostgreSQL-based service) 
  - `dev:venn` - Changes affecting development tools, build system, or cross-cutting concerns
- **Priority Label**: Must apply one priority from P0 (highest) to P4 (lowest)
  - `P0` - Critical/blocking issues
  - `P1` - High priority
  - `P2` - Medium priority  
  - `P3` - Low priority
  - `P4` - Lowest priority/nice-to-have
