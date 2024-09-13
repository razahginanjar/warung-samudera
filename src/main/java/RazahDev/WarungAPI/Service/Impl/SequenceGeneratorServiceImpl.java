package RazahDev.WarungAPI.Service.Impl;

import RazahDev.WarungAPI.Entity.SequenceGenerator;
import RazahDev.WarungAPI.Repository.SequenceGeneratorRepository;
import RazahDev.WarungAPI.Service.SequenceGeneratorService;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SequenceGeneratorServiceImpl implements SequenceGeneratorService {


    private final SequenceGeneratorRepository sequenceGeneratorRepository;

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Integer getReceiptNumber()
    {
        if (sequenceGeneratorRepository.existsById("receipt_sequence"))
        {
            SequenceGenerator sequence = sequenceGeneratorRepository.findById("receipt_sequence").orElse(null);
            assert sequence != null;
            Integer oldId = sequence.getId();
            sequence.setId(oldId + 1);
            sequenceGeneratorRepository.save(sequence);
            sequenceGeneratorRepository.flush();
            return sequence.getId();
        }
        SequenceGenerator sequenceGenerator = new SequenceGenerator();
        sequenceGenerator.setName("receipt_sequence");
        sequenceGenerator.setId(1);
        sequenceGeneratorRepository.save(sequenceGenerator);
        sequenceGeneratorRepository.flush();
        return sequenceGenerator.getId();
    }
}
