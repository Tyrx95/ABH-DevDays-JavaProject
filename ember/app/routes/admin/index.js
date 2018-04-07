import Ember from 'ember';

const {
  inject: {
    service,
  },
} = Ember;

export default Ember.Route.extend({
  ajax: service('ajax'),

  model(){
    return Ember.RSVP.hash({
      statistics: this.get('ajax').request('/admin/getAdministratorStatistics')
    });
  }


});
