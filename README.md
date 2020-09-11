Here are some comments to explain my development process and choice of tools

To maintain the decoupling of the layers, follow the SOLID principles and thus guarantee maintenance flexibility, I decided to use the MVVM architecture with Observer and LiveData.
To make API calls, I used Retrofit2 with Dagger2 for the simplicity and to keep the code decoupled.I followed TDD development to code the API calls so I could ensure
that the logic after the response were correct, ensuring data quality. To enhance produtivity, I used navigation to create the fragments flows and transiction actions.
In addition to the main objective of the challenge, I implemented three extra objectives:
- Offline functionality
- Use of kotlin as programming language
- Espresso to execute the UI test cases

The solution efficiently delivers the challenge, but I see that there is still room for enhancement, adding more test cases to UI and increasing the unit test coverage

Ps. As it uses lattest gradlle version, you need to run it using Android Studio 4.0 version or higher
