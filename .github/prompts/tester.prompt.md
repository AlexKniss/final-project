---
agent: "agent"
tools: ['my-mcp-server-dfb71170/*']
description: "description of the tool"
model: 'GPT-5 mini'
---
## Follow instruction below: ##
1. run `mvn clean`
2. generate tests for `BitField.java`
3. run the generated tests using `mvn test`
4. fix any bugs that occur in any failing tests
5. Parse `jacoco.xml` to get code coverage report after running tests.
6. Write additional tests to increase code coverage.
7. Repeat steps 2-6 until code coverage is at least 90%.
