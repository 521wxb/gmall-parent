if redis.call("EXISTS",KEYS[1])
then
    return redis.call("del",KEYS[1])
else
    return 0
end
