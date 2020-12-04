--
-- User: 我的我的
-- Date: 20/12/4
-- Time: 21:44
-- To change this template use File | Settings | File Templates.
--

-- 这个脚本主要是用来取消消息的, 实现是把消息重新返回到 队列中

local cancelScript = "local list_key = KEYS[1]; local random_str_key = KEYS[2]; local res = {}; local opt_value = redis.call('get', random_str_key); redis.call('del', random_str_key); redis.call('rpush', list_key, opt_value); res[1] = 1; res[2] = random_str_key; res[3] = opt_value; return res;";

local list_key = KEYS[1];
local random_str_key = KEYS[2];
local res = {};


local opt_value = redis.call('get', random_str_key);

redis.call('del', random_str_key);

redis.call('rpush', list_key, opt_value);

res[1] = 1;
res[2] = random_str_key;
res[3] = opt_value;
return res;