redis.call("del" , KEYS[1])
redis.call("del" , "{"..KEYS[1].."}:config")
redis.call("rename" , KEYS[2] , KEYS[1])
redis.call("rename" , "{"..KEYS[2].."}:config" , "{"..KEYS[1].."}:config")
return 1
