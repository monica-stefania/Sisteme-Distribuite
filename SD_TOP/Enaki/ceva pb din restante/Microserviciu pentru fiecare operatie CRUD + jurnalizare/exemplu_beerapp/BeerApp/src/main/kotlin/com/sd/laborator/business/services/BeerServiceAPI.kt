package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.IBeerServiceAPI
import com.sd.laborator.business.interfaces.crudInterfaces.*
import com.sd.laborator.models.Beer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class BeerServiceAPI: IBeerServiceAPI {
    @Autowired
    private lateinit var _beerAdder: IBeerAdder

    @Autowired
    private lateinit var _beerDeleter: IBeerDeleter

    @Autowired
    private lateinit var _beerGetterByName: IBeerGetterByName

    @Autowired
    private lateinit var _beerGetterByPrice: IBeerGetterByPrice

    @Autowired
    private lateinit var _beersGetter: IBeersGetter

    @Autowired
    private lateinit var _beerTableCreator: IBeerTableCreator

    @Autowired
    private lateinit var _beerUpdater: IBeerUpdater

    override fun createBeerTable() {
        _beerTableCreator.createBeerTable()
    }

    override fun addBeer(beer: Beer)=_beerAdder.addBeer(beer)

    override fun getBeers(): String=_beersGetter.getBeers()

    override fun getBeerByName(name: String): String?=_beerGetterByName.getBeerByName(name)

    override fun getBeerByPrice(price: Float): String=_beerGetterByPrice.getBeerByPrice(price)

    override fun updateBeer(beer: Beer)=_beerUpdater.updateBeer(beer)

    override fun deleteBeer(name: String)=_beerDeleter.deleteBeer(name)
}