import React from 'react';
import { StyleSheet, Text, Button, View } from 'react-native';

export default class App extends React.Component {
  render() {
    return (
      <View style={styles.container}>
        <Text>Select an action you wish to perform</Text>
        <Button
        //  onPress={this._onPressButton}
            title="I Blocked U"
            color="#841584"
        />
        <Button
        //  onPress={this._onPressButton}
            title="Who's blocking me?"
        />
        <Button
        //  onPress={this._onPressButton}
            title="Going Home!"
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
