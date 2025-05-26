#include <iostream>
using namespace std;

// 判斷是否為質數的函式
bool isPrime(int n) {
    if (n <= 1) return false;
    for (int i = 2; i * i <= n; ++i) {
        if (n % i == 0) return false;
    }
    return true;
}

int main() {
    int num;
    cin >> num;

    if (isPrime(num)) {
        cout << "Yes\n";
    } else {
        cout << "No\n";
    }

    return 0;
}
