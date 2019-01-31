import React from "react";
import { View, Text, TouchableOpacity } from "react-native";

const ListPage = ({ navigation }) => (
  <View>
    <TouchableOpacity onPress={() => navigation.navigate("ItemPage")}>
      <Text>GO DEEPER</Text>
    </TouchableOpacity>
  </View>
);

export default ListPage;
