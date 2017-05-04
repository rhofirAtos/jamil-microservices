package org.test;

import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
interface ReservationServiceChannels {
	@Input
	SubscribableChannel input();
}


@EnableBinding(ReservationServiceChannels.class)
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationServiceApplication {
	@Component
	class SampleRecordsCLR implements CommandLineRunner {

		private final ResevationRepository reservationRepository;

		@Autowired
		public SampleRecordsCLR(ResevationRepository reservationRepository) {
			this.reservationRepository = reservationRepository;
		}

		@Override
		public void run(String... args) throws Exception {
			Stream.of("Josh", "Jungryeol", "Nosung", "Hyobeom", "Soeun", "Seunghue", "Peter", "Jooyong")
					.forEach(name -> reservationRepository.save(new Reservation(name)));

			reservationRepository.findAll().forEach(System.out::println);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}
}

@RepositoryRestResource
interface ResevationRepository extends JpaRepository<Reservation, Long> {
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
class Reservation {
	@Id
	@GeneratedValue
	private Long id;
	private String reservationName;

	public Reservation(String reservationName) {
		this.reservationName = reservationName;
	}
}


@MessageEndpoint
class ReservationProcessor {

	private final ResevationRepository reservationRepository;

	@Autowired
	public ReservationProcessor(ResevationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@ServiceActivator(inputChannel = "input")
	public void acceptNewReservations(Message<String> msg) {
		String rn = msg.getPayload();

		this.reservationRepository.save(new Reservation(rn));
	}
}