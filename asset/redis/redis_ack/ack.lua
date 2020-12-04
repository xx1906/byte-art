--
-- User: 我的我的
-- Date: 20/12/4
-- Time: 21:41
-- To change this template use File | Settings | File Templates.
--

local ackScript ="local random_str_key = KEYS[1]; return redis.call('del',random_str_key);";

-- 这个是实现消息的确认功能, 主要就是删除string
local random_str_key = KEYS[1];
return redis.call('del',random_str_key);