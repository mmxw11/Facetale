package wepa.ftale.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import wepa.ftale.domain.FtImage;

/**
 * @author Matias
 */
public interface ImageRepository extends JpaRepository<FtImage, UUID> {
}