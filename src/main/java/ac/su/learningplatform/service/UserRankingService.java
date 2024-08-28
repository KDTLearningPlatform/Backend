package ac.su.learningplatform.service;

import ac.su.learningplatform.dto.UserRankingDTO;
import ac.su.learningplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserRankingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RANKING_CACHE_KEY = "userRankingCache";

    public List<UserRankingDTO> getUserRanking() {
        // Redis에서 캐시된 랭킹 데이터를 가져옴
        List<UserRankingDTO> cachedRanking = (List<UserRankingDTO>) redisTemplate.opsForValue().get(RANKING_CACHE_KEY);

        // 캐시된 데이터가 없으면 데이터베이스에서 조회하고, Redis에 캐싱함
        if (cachedRanking == null) {
            cachedRanking = fetchAndCacheUserRanking();
        }

        return cachedRanking;
    }

    // 새로운 랭킹 데이터를 가져오고 캐시하는 메서드
    public List<UserRankingDTO> fetchAndCacheUserRanking() {
        List<UserRankingDTO> rankingList = userRepository.findTop20ByOrderByTotalPointDescUpdateDateDesc()
                .stream()
                .map(user -> new UserRankingDTO(
                        user.getProfileImage(),
                        user.getNickname(),
                        user.getTotalPoint()
                ))
                .collect(Collectors.toList());

        // Redis에 캐싱, 10분 동안 캐시 유지
        redisTemplate.opsForValue().set(RANKING_CACHE_KEY, rankingList, 10, TimeUnit.MINUTES);

        return rankingList;
    }

    // 필요 시 캐시를 무효화하는 메서드, 데이터 갱신 시 호출할 수 있음
    public void invalidateRankingCache() {
        redisTemplate.delete(RANKING_CACHE_KEY);
    }

    // 포인트 업데이트 시 호출하여 캐시 갱신
    public void updateRankingCache() {
        invalidateRankingCache(); // 기존 캐시를 무효화
        fetchAndCacheUserRanking(); // 새로운 데이터를 캐싱
    }
}
