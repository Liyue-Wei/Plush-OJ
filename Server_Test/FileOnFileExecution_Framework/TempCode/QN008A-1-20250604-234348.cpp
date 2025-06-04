#include <iostream>
using namespace std;

// 判斷是否為質數的函式
bool isPrime(int n) {
    if (n <= 1) return false;

    int i = 2;
    while (i * i <= n) {
        if (n % i == 0) return false;
        ++i;
    }

    return true;
}

int main() {
    int num;
    while (cin >> num) { // 持續從輸入讀取整數
        if (isPrime(num)) {
            cout << "Yes\n";
        } else {
            cout << "No\n";
        }
    }

    return 0;
}
