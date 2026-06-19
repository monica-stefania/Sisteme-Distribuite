package com.sd.laborator.business.abstractClasses

import com.sd.laborator.persistence.interfaces.IBeerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
abstract class AbstractBeerCrudService {
    @Autowired
    protected lateinit var _beerRepository: IBeerRepository
    protected val _pattern: Pattern = Pattern.compile("\\W")
}