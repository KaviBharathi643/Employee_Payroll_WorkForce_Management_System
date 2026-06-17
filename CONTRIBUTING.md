# Contributing to Employee Payroll & Workforce Management System

First off, thank you for considering contributing to our project! It's people like you that make this system such a great tool.

## Code of Conduct

This project and everyone participating in it is governed by our Code of Conduct. By participating, you are expected to uphold this code.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

* **Use a clear and descriptive title**
* **Describe the exact steps which reproduce the problem**
* **Provide specific examples to demonstrate the steps**
* **Describe the behavior you observed after following the steps**
* **Explain which behavior you expected to see instead and why**
* **Include screenshots and animated GIFs if possible**
* **Include your environment details** (OS, Java version, Node version, etc.)

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* **Use a clear and descriptive title**
* **Provide a step-by-step description of the suggested enhancement**
* **Provide specific examples to demonstrate the steps**
* **Describe the current behavior and the expected behavior**
* **Explain why this enhancement would be useful**

## Development Setup

### Prerequisites

- Java 21 (JDK)
- Maven 3.9+
- Node.js 18+
- MySQL 8.0+
- Git

### Getting Started

1. **Fork the repository**
   ```bash
   # On GitHub, click "Fork"
   ```

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR_USERNAME/Employee_Payroll_WorkForce_Management_System.git
   cd Employee_Payroll_WorkForce_Management_System
   ```

3. **Add upstream remote**
   ```bash
   git remote add upstream https://github.com/KaviBharathi643/Employee_Payroll_WorkForce_Management_System.git
   ```

4. **Create your feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

### Backend Development

1. **Navigate to backend**
   ```bash
   cd backend
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Run tests**
   ```bash
   mvn test
   ```

### Frontend Development

1. **Navigate to frontend**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm run dev
   ```

4. **Run linter**
   ```bash
   npm run lint
   ```

5. **Build for production**
   ```bash
   npm run build
   ```

## Commit Message Convention

We follow the Conventional Commits specification for commit messages:

### Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat**: A new feature
- **fix**: A bug fix
- **docs**: Documentation only changes
- **style**: Changes that do not affect the meaning of the code (formatting, missing semicolons, etc.)
- **refactor**: A code change that neither fixes a bug nor adds a feature
- **perf**: A code change that improves performance
- **test**: Adding missing tests or correcting existing tests
- **chore**: Changes to build process, dependencies, or tools
- **ci**: Changes to CI/CD configuration

### Examples

Good commit messages:
```
feat(auth): add JWT token refresh endpoint

fix(attendance): correct check-out time calculation

docs(readme): update installation instructions

style(backend): format Employee entity

refactor(service): extract common validation logic

test(payroll): add comprehensive payroll calculation tests
```

### Scope

The scope should specify what area of the codebase is affected:

**Backend scopes:**
- `auth`, `employee`, `attendance`, `leave`, `payroll`, `payslip`, `report`, `settings`, `notification`, `security`

**Frontend scopes:**
- `components`, `pages`, `services`, `hooks`, `context`, `utils`, `api`, `routes`, `layout`

### Subject

- Use the imperative mood ("add" not "added" or "adds")
- Don't capitalize the first letter
- No period (.) at the end
- Limit to 50 characters

### Body

- Explain *what* and *why*, not *how*
- Wrap at 72 characters
- Separate from subject by a blank line

### Footer

- Reference related issues: `Closes #123`
- Breaking changes: `BREAKING CHANGE: description`

## Branch Naming Convention

We use the following branch naming convention:

```
<type>/<short-description>
```

### Types

- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation updates
- `refactor/` - Code refactoring
- `test/` - Test additions
- `chore/` - Maintenance tasks

### Examples

```
feature/jwt-refresh-tokens
fix/attendance-calculation-bug
docs/api-documentation
refactor/payroll-service
test/employee-controller
chore/update-dependencies
```

## Pull Request Process

1. **Update your branch**
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Create a descriptive PR title**
   - Follow the same conventions as commit messages
   - Be specific about what changes are included

3. **Fill out the PR template**
   - Description of changes
   - Related issue(s)
   - Type of change (feature, fix, docs, etc.)
   - Testing performed
   - Screenshots (if applicable)

4. **Ensure all checks pass**
   - Code builds successfully
   - Tests pass
   - No linting errors
   - Commits follow the convention

5. **Request review from maintainers**

6. **Address review comments**
   - Make requested changes in new commits
   - Respond to all feedback
   - Re-request review when ready

7. **Squash commits if requested**
   ```bash
   git rebase -i upstream/main
   ```

## Style Guides

### Java Backend

- Use 4 spaces for indentation
- Follow Google Java Style Guide
- Use meaningful variable names
- Add Javadoc comments for public methods
- Keep methods focused and under 30 lines
- Use Spring Boot conventions

Example:
```java
/**
 * Retrieves an employee by their ID.
 * 
 * @param id the employee ID
 * @return the employee DTO
 * @throws EntityNotFoundException if employee not found
 */
@GetMapping("/{id}")
public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long id) {
    EmployeeDTO employee = employeeService.getEmployeeById(id);
    return ResponseEntity.ok(employee);
}
```

### React Frontend

- Use 2 spaces for indentation
- Use functional components with hooks
- Use meaningful component names (PascalCase)
- Use meaningful variable names (camelCase)
- Add JSDoc comments for complex functions
- Keep components focused and under 200 lines

Example:
```javascript
/**
 * Employee list component with search and filter capabilities
 * @returns {JSX.Element} The employee list component
 */
export function EmployeeList() {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    try {
      setLoading(true);
      const data = await employeeService.getEmployees();
      setEmployees(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // ... component JSX
}
```

## Testing

### Backend Tests

- Write unit tests for services
- Write integration tests for controllers
- Use JUnit 5 and Mockito
- Aim for 70%+ code coverage
- Run tests before committing

```bash
mvn test
```

### Frontend Tests

- Write tests for components
- Use testing library
- Test user interactions, not implementation
- Write descriptive test names

```bash
npm test
```

## Additional Notes

### Issue and Pull Request Labels

- `bug` - Something isn't working
- `documentation` - Improvements or additions to documentation
- `duplicate` - This issue or PR already exists
- `enhancement` - New feature or request
- `good first issue` - Good for newcomers
- `help wanted` - Extra attention is needed
- `invalid` - This doesn't seem right
- `question` - Further information is requested
- `wontfix` - This will not be worked on

### Recognition

Contributors will be recognized in:
- README.md contributors section
- GitHub contributors page
- Project changelog

## Questions?

Feel free to open an issue with the label `question` or reach out to the maintainers.

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing! 🎉
