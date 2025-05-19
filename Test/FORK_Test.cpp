#include <iostream>
#include <unistd.h> // For fork()
#include <sys/wait.h> // For wait()

int main() {
    pid_t child_pid;

    // Create a child process
    child_pid = fork();

    if (child_pid < 0) {
        // Fork failed
        std::cerr << "Fork failed!" << std::endl;
        return 1;
    } else if (child_pid == 0) {
        // This is the child process
        std::cout << "Hello from the child process! My PID is " << getpid() << std::endl;
        // Child process can do some work here
        // For example, execute another program using exec() family functions
        // execlp("/bin/ls", "ls", "-l", NULL); // Example: list files
        std::cout << "Child process finishing." << std::endl;
    } else {
        // This is the parent process
        std::cout << "Hello from the parent process! My PID is " << getpid() << std::endl;
        std::cout << "My child's PID is " << child_pid << std::endl;

        // Wait for the child process to terminate
        int status;
        waitpid(child_pid, &status, 0); // Or simply wait(&status);

        if (WIFEXITED(status)) {
            std::cout << "Child process exited with status " << WEXITSTATUS(status) << std::endl;
        } else {
            std::cout << "Child process terminated abnormally." << std::endl;
        }
        std::cout << "Parent process finishing." << std::endl;
    }

    return 0;
}