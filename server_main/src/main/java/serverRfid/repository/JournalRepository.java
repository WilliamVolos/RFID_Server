package serverRfid.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import serverRfid.model.Journal;

import java.util.List;

public interface JournalRepository extends ListCrudRepository<Journal, Long> {

    @Modifying
    @Query("""
            INSERT INTO journal \s
            (datetime, visible, port_id, epc, rssi) \s
            SELECT jj.datetime + make_interval(secs => 3), false as visible, jj.port_id, jj.epc, '0' rssi \s
            FROM ( SELECT j.datetime, j.visible, j.port_id, j.epc, ROW_NUMBER() OVER (PARTITION BY j.port_id, j.epc  ORDER BY j.datetime DESC) AS rn \s
                     FROM journal j, ports p \s
                    WHERE p.reader_id = 3 \s
                      AND j.port_id = p.id) jj \s
            WHERE jj.rn = 1 \s
              AND jj.visible = true; \s
            """)
    void closeVisibleTagsByReaderId(@Param("readerId") long readerId, @Param("delaySec") int delayVisibleTag);


    @Query("""
            SELECT j.id, j.datetime, j.visible, j.port_id, j.epc, j.rssi \s
            FROM journal j, ports p \s 
            where p.reader_id = :readerId \s
             and j.port_id = p.id \s
            ORDER BY j.datetime desc \s
            LIMIT :limit; 
            """)
    List<Journal> findLastByReaderIdAndLimit(@Param("readerId") long readerId, @Param("limit") int limit);
}
