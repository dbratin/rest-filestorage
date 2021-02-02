package test.task.db.storage;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import test.task.db.storage.model.FileRecordEntity;

@Repository
public interface FilesRegisterRepository extends PagingAndSortingRepository<FileRecordEntity, Long> {

    @Modifying
    @Query("update FileRecordEntity set version = version + 1 where id=:id and version=:version")
    int acquireFileRecord(Long id, Integer version);
}
