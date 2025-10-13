package by.story_weaver.ridereserve.Logic.hilt.modules;

import java.util.concurrent.Executor;

import javax.inject.Singleton;

import by.story_weaver.ridereserve.Logic.data.repositories.impl.BookingRepositoryImpl;
import by.story_weaver.ridereserve.Logic.data.repositories.impl.CommunicationLogRepositoryImpl;
import by.story_weaver.ridereserve.Logic.data.repositories.impl.RouteRepositoryImpl;
import by.story_weaver.ridereserve.Logic.data.repositories.impl.SeatRepositoryImpl;
import by.story_weaver.ridereserve.Logic.data.repositories.impl.SupportRepositoryImpl;
import by.story_weaver.ridereserve.Logic.data.repositories.impl.TicketMessageRepositoryImpl;
import by.story_weaver.ridereserve.Logic.data.repositories.impl.TripRepositoryImpl;
import by.story_weaver.ridereserve.Logic.data.repositories.impl.UserRepositoryImpl;
import by.story_weaver.ridereserve.Logic.data.repositories.impl.VehicleRepositoryImpl;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.BookingRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.CommunicationLogRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.RouteRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.SeatRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.SupportRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.TicketMessageRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.TripRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.UserRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.VehicleRepository;
import by.story_weaver.ridereserve.Logic.utils.AppExecutor;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;


@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds @Singleton public abstract UserRepository bindUserRepository(UserRepositoryImpl impl);
    @Binds @Singleton public abstract RouteRepository bindRouteRepository(RouteRepositoryImpl impl);
    @Binds @Singleton public abstract VehicleRepository bindVehicleRepository(VehicleRepositoryImpl impl);
    @Binds @Singleton public abstract SeatRepository bindSeatRepository(SeatRepositoryImpl impl);
    @Binds @Singleton public abstract TripRepository bindTripRepository(TripRepositoryImpl impl);
    @Binds @Singleton public abstract BookingRepository bindBookingRepository(BookingRepositoryImpl impl);
    @Binds @Singleton public abstract SupportRepository bindSupportRepository(SupportRepositoryImpl impl);
    @Binds @Singleton public abstract TicketMessageRepository bindTicketMessageRepository(TicketMessageRepositoryImpl impl);
    @Binds @Singleton public abstract CommunicationLogRepository bindCommunicationLogRepository(CommunicationLogRepositoryImpl impl);
    @Binds @Singleton public abstract Executor bindExecutor(AppExecutor impl);
}
