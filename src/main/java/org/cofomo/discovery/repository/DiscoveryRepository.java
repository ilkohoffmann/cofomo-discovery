package org.cofomo.discovery.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.cofomo.discovery.domain.MobilityProvider;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscoveryRepository extends CrudRepository<MobilityProvider, String>, MobilityProviderGeoLocation {

	// no heartbeat check
	public List<MobilityProvider> findByServiceOffersIn(List<String> ms);

	// no heartbeat check
	public List<MobilityProvider> findByOperationAreas(int postcode);

	public List<MobilityProvider> findByLastHeartBeatBetweenAndServiceOffersIn(LocalDateTime publicationTimeStart,
			LocalDateTime publicationTimeEnd, List<String> ms);

	public List<MobilityProvider> findByLastHeartBeatBetweenAndOperationAreas(LocalDateTime publicationTimeStart,
			LocalDateTime publicationTimeEnd, int postcode);

	public Optional<MobilityProvider> findAllByLastHeartBeatBetweenAndId(LocalDateTime publicationTimeStart,
			LocalDateTime publicationTimeEnd, String id);

	public List<MobilityProvider> findAllByLastHeartBeatBetween(LocalDateTime publicationTimeStart,
			LocalDateTime publicationTimeEnd);
}
