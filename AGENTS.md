# AGENTS Guide for `dsd`

## Scope and current AI conventions
- Source search run for `**/{.github/copilot-instructions.md,AGENT.md,AGENTS.md,CLAUDE.md,.cursorrules,.windsurfrules,.clinerules,.cursor/rules/**,.windsurf/rules/**,.clinerules/**,README.md}` found no existing AI-rule files.
- This file is the canonical agent guidance for this repository.

## Architecture map (Spring Boot monolith)
- Entry point: `src/main/java/com/onesolutions/dsd/DsdApplication.java`.
- API layer: controllers in `src/main/java/com/onesolutions/dsd/controller`.
- Business layer: interfaces in `service/` and implementations in `serviceimpl/`.
- Persistence layer: Spring Data JPA repos in `repository/`, entities in `entity/`.
- DTO boundary: all HTTP payloads are in `dto/`; services map DTO <-> entity manually.
- App base path is `server.servlet.context-path=/api/v1` in `src/main/resources/application.properties`.

## High-value data flows to understand first
- User registration: `authController.register` -> `userServiceImpl.registerUser` -> `profileRepo.save` -> `EmailService.sendEmail` using Thymeleaf template `templates/registration-mail.html`.
- HP patch: `PATCH /users/{id}/hp` increments `UserEntity.hp` in `userServiceImpl.addHp`.
- Tournament join: `TournamentController.joinTournament` -> `TournamentServiceImpl.joinTournament`; validates `expired` + duplicate membership, mutates `Tournament.usersJoined` (`@ManyToMany`) and increments `totalJoined`.
- Team creation: `TournamentServiceImpl.createTeam` saves `Team`, resolves each gamer name via `profileRepo.findByGamerName`, then writes `TeamMember` rows.
- Team listing: `getTeamsByTournament` loads teams, then members, then users (N+1 style lookup) to build `TeamResponseDTO`.

## Project-specific conventions (follow existing patterns)
- Naming is intentionally inconsistent with Java norms in several core types: `authController`, `userService`, `userServiceImpl`, `profileRepo`.
- Preserve existing public API names and routes unless explicitly asked to refactor.
- Controllers usually return DTOs or success strings, and business failures currently throw `RuntimeException` (no global exception handler yet).
- Mapping is done inline in service impls (`toEntity`, `toDto`) instead of mapper libraries.
- Lombok is used heavily (`@Data`, `@Builder`, `@RequiredArgsConstructor`) for DTOs/entities/services.

## Integration points and dependencies
- Build/runtime stack from `pom.xml`: Spring Boot 4.0.3, Java 21, Web MVC, Validation, JPA, Mail, Thymeleaf, MySQL.
- DB defaults in `application.properties`: `jdbc:mysql://localhost:3306/dsd`, username/password `root`/`root`.
- Mail uses Gmail SMTP and requires env var `GMAIL_APP_PASSWORD`; sender is currently hardcoded in `EmailServiceImpl`.
- SQL logging is enabled (`spring.jpa.show-sql=true`, `hibernate.format_sql=true`) and useful for debugging query behavior.

## Developer workflows (Windows PowerShell)
- Run app: `./mvnw.cmd spring-boot:run`
- Run tests: `./mvnw.cmd test`
- Build jar: `./mvnw.cmd clean package`
- Hit endpoints under `/api/v1`, for example `/api/v1/tournaments` and `/api/v1/register`.

## Known structure quirks to keep in mind
- `GameCategory.java` lives under `entity/` path but declares package `com.onesolutions.dsd.enums`; keep package/imports consistent when moving files.
- `TournamentRepository` declares `findByTournamentId` returning `List<Team>`; `TeamRepository` also has this query and is the one used by services.
- Test coverage is minimal (`DsdApplicationTests.contextLoads`), so verify behavior with endpoint-level checks when changing service logic.

