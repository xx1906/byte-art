import axios, { AxiosRequestConfig, AxiosResponse } from "axios";

/*
{
  "data": {
    "address":"xx",
    "age":18,
    "id":"001"
  },
  "err_no": 10000,
  "err_msg":"success"
}
*/
// 定义 response
export interface BizResponse<T extends unknown = any> {
    data: T; // data 字段值
    err_no: number;
    err_msg: string;
}

const instance = axios.create({ baseURL: "http://localhost:28889" });

// 响应体拦截器
const ff = (res: AxiosResponse<BizResponse, any>) => {
    if (res.data.err_no != 10000) {
        return Promise.reject(res);
    }
    return Promise.resolve(res);
};

const ee = (err: any) => {
    return Promise.reject(err);
};

// 设置拦截器
instance.interceptors.response.use(ff, ee);

// export const get = <T>(url: string, params: any) => {
//   return new Promise<BizResponse<T>>((resolve, reject) => {
//     instance
//       .get(url, { params: params })
//       .then((res) => {
//         resolve(res.data);
//       })
//       .catch((err) => {
//         reject(err.data);
//       });
//   });
// };

const request = <RES, REQ = any>(config: AxiosRequestConfig<REQ>) => {
    config.headers = config.headers ? config.headers : {};
    config.headers["Token"] = "token";
    console.log("header", config.headers);

    return new Promise<BizResponse<RES>>((resolve, reject) => {
        instance
            .request(config)
            .then((res) => {
                resolve(res.data);
            })
            .catch((err) => reject(err));
    });
};

export const Get = <RES, REQ = any>(url: string, params?: REQ) => {
    return request<RES, REQ>({ url: url, params: params, method: "GET" });
};

export const Post = <RES, REQ = any>(url: string, params?: any, data?: REQ) => {
    return request<RES, REQ>({ url: url, data: data, method: "POST" });
};

// request({ url: "/hello/world", params: { err_no: 10000099999 } }).then((res) =>
//   console.log(res)
// );
