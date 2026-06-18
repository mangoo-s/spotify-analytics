package statistics.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import statistics.entity.ArtistStatsEntity;

public interface ArtistStatsRepository extends JpaRepository<ArtistStatsEntity, Long> {
    @Modifying
    @Query("""
    UPDATE ArtistStatsEntity arts
    SET arts.totalPlays = arts.totalPlays + 1
    WHERE arts.artistId= :artistId
    """)
    int incrementTotalPlays(@Param("artistId") Long artistId);

}
