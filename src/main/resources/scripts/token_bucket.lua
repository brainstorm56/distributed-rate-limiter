
local key = KEYS[1]
local now = tonumber(ARGV[1])

if redis.call('EXISTS', key) == 0 then
    return {-1, 0, 0, 0}
end

local capacity = tonumber(redis.call('HGET', key, 'capacity'))
local refillRate = tonumber(redis.call('HGET', key, 'refillRate'))
local currentTokens = tonumber(redis.call('HGET', key, 'currentTokens'))
local lastRefillTime = tonumber(redis.call('HGET', key, 'lastRefillTime'))

if not now or not capacity or not refillRate
        or not currentTokens or not lastRefillTime then
    return redis.error_reply("Invalid bucket state")
end

local elapsed = (now - lastRefillTime) / 1000.0

local potentialTokens = math.floor(elapsed * refillRate)

local availableSpace = capacity - currentTokens

local tokensToAdd = math.min(availableSpace, potentialTokens)

currentTokens = currentTokens + tokensToAdd

if currentTokens == capacity then
    lastRefillTime = now
else
    lastRefillTime = lastRefillTime
        + (potentialTokens / refillRate) * 1000
end

local allowed = 0

if currentTokens >= 1 then
    currentTokens = currentTokens - 1
    allowed = 1
end

local retryAfter = 0

if allowed == 0 then
    retryAfter = math.max(
        0,
        (1 / refillRate) - elapsed
    )
end

redis.call(
    'HSET',
    key,
    'currentTokens', currentTokens,
    'lastRefillTime', lastRefillTime
)

return {
    allowed,
    currentTokens,
    capacity,
    retryAfter
}