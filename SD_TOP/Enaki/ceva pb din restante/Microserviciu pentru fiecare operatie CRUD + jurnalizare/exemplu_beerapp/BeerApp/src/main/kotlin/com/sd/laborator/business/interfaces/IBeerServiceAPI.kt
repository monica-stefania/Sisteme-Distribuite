package com.sd.laborator.business.interfaces

import com.sd.laborator.business.interfaces.crudInterfaces.*
import com.sd.laborator.models.Beer

interface IBeerServiceAPI:
    IBeerAdder, IBeerDeleter, IBeerGetterByName, IBeerGetterByPrice,
    IBeersGetter, IBeerTableCreator, IBeerUpdater