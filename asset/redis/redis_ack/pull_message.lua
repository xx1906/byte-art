--
-- User: 我的我的
-- Date: 20/12/4
-- Time: 21:34
-- To change this template use File | Settings | File Templates.
--
local pullScript = "local list_key = KEYS[1]; local random_str_key = KEYS[2]; local res = {}; local opt_value = redis.call('lpop', list_key); if (not opt_value) then res[1] = 2; res[2] = random_str_key; res[3] = 'nil'; return res; end redis.call('set', random_str_key, opt_value); res[1] = 1; res[2] = random_str_key; res[3] = opt_value; return res;"

-- 处理客户端传入的参数 一个是 list key, 一个是 random_str_key 也可以是分布式 id, 反正不重复就好
local list_key = KEYS[1];
local random_str_key = KEYS[2];
local res = {};

local opt_value = redis.call('lpop', list_key);
if (not opt_value) then
    res[1] = 2;
    res[2] = random_str_key;
    res[3] = 'nil';
    return res;
end

redis.call('set', random_str_key, opt_value);
res[1] = 1;
res[2] = random_str_key;
res[3] = opt_value;

return res;



