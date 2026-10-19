
package com.ecosmart.ecosmart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmartBinRepository
        extends JpaRepository<SmartBin, Long> {
}

