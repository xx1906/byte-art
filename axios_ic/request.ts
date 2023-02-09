import axios, { AxiosRequestConfig, AxiosResponse } from "axios";

// Avocado
export interface Avocado<T extends unknown = any> {
    data: T; // data 字段值
    err_no: number; // 业务代码
    err_msg: string; // 响应错误消息
}
// 获取基础 url
const url = import.meta.env.VITE_BASE_URL;
const instance = axios.create({ baseURL: url, timeout: 30_000 });

// 响应体拦截器
const interceptor = (res: AxiosResponse<Avocado, any>) => {
    if (res.data.err_no != 10000) {
        return Promise.reject(res);
    }
    return Promise.resolve(res);
};

// 设置拦截器
instance.interceptors.response.use(interceptor, (err: any) => {
    return Promise.reject(err);
});

// request 通用请求
export const Request = <RES /* 响应参数 */, REQ = any>(
    config: AxiosRequestConfig<REQ>
) => {
    config.headers = config.headers ? config.headers : {};

    return new Promise<Avocado<RES>>((resolve, reject) => {
        instance
            .request(config)
            .then((res: AxiosResponse<Avocado<RES>>) => {
                resolve(res.data);
            })
            .catch((err: any) => reject(err));
    });
};

export const Get = <RES, REQ = any>(url: string, params?: REQ) => {
    return Request<RES, REQ>({ url: url, params: params, method: "GET" });
};

export const Post = <RES, REQ = any>(url: string, data?: REQ) => {
    return Request<RES, REQ>({ url: url, data: data, method: "POST" });
};

export const Put = <RES, REQ = any>(url: string, data?: REQ) => {
    return Request<RES, REQ>({ url: url, data: data, method: "PUT" });
};
