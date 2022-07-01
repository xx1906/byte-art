#include <iostream>
#include <chrono>
#include <thread>

using ll = long long;

using namespace std;
using namespace chrono;

ll fibonacci(ll n);

int main(void)
{
    // 计算函数的运行耗时
    milliseconds start_time = duration_cast<milliseconds>(system_clock::now().time_since_epoch());
    std::cout << "start_time:" << start_time.count() << endl;
    //
    // 在这里运行耗时的函数
    //
    fibonacci(60); // 这里表示 2 ^ 60 这么大的运算规模的, 相当大的计算量了
    milliseconds end_time = duration_cast<milliseconds>(system_clock::now().time_since_epoch());
    std::cout << "end_time:" << end_time.count() << endl;
    cout << "cost:" << milliseconds(end_time).count() - milliseconds(start_time).count() << "ms" << endl;
    return 0;
}

ll fibonacci(ll n)
{
    if (n <= 0)
    {
        return 0;
    }

    if (n == 1)
    {
        return 1;
    }

    return fibonacci(n - 1) + fibonacci(n - 2);
}