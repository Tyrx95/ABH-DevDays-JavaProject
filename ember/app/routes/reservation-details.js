import Ember from 'ember';

const {
  inject: {
    service,
  },
} = Ember;

export default Ember.Route.extend({
  ajax: service('ajax'),

  model(params) {
    return Ember.RSVP.hash({
      reservation: this.get('ajax').request('/getReservation/' + params.reservation_id),
      cities: this.get('ajax').request('/getAllCities'),
      user: this.get('ajax').request('/getCurrentUser', {
        xhrFields: {
          withCredentials: true,
        },
      }),
    });
  },

  afterModel(model, transition) {
    var self = this;
    Ember.run.later(function () {
      self.checkReservationStatus(model.reservation.id);
    }, 300000);
    transition.send('restaurant', model.reservation.table.restaurantId);
  },

  checkReservationStatus(reservationId){
    var self = this;
    this.get('ajax').request('/getReservation/' + reservationId)
      .then(
        (response) => {
          if(!response.confirmed){
            self.deleteExpiredReservation(reservationId);
          }
        }, (error) => alert(error)
      );
  },

  deleteExpiredReservation(reservationId){
    this.get('ajax').del('/deleteReservation/' + reservationId, {
      xhrFields: {
        withCredentials: true,
      },
    })
      .then(
        () => {console.log('Reservation deleted');},
        (error) =>  alert(error)
      );
  },

  actions: {
    restaurant: function (restaurantId) {
        this.get('ajax').request('/getRestaurant/' + restaurantId)
        .then((repsonse) => this.controllerFor('reservation-details').set('restaurant', repsonse));
      },
  },
});
